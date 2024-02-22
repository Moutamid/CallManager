package com.moutamid.callmanager;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.callmanager.adapters.ContactsAdapter;
import com.moutamid.callmanager.databinding.ActivityMainBinding;
import com.moutamid.callmanager.models.ContactModel;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public final int PICK_CONTACT_REQUEST = 1;
    ContactsAdapter adapter;
    ArrayList<ContactModel> list = new ArrayList<>();
    String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
    };
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        askPermissions();

        list = Stash.getArrayList(Constants.CONTACTS, ContactModel.class);
        list.sort(Comparator.comparing(ContactModel::getContactName));

        binding.addContact.setOnClickListener(v -> {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, 1222);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 2);
                } else {
                    getContact();
                }
            }
        });

        binding.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Contacts")
                    .setMessage("Are you sure you want clear all contacts?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        list.clear();
                        adapter.notifyDataSetChanged();
                        Stash.put(Constants.CONTACTS, list);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        binding.selectAll.setOnClickListener(v -> {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, 1222);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 3);
                } else {
                    selectAll();
                }
            }
        });

        binding.contactRc.setLayoutManager(new LinearLayoutManager(this));
        binding.contactRc.setHasFixedSize(false);

        adapter = new ContactsAdapter(this, list);
        Log.d("CHECK123", "SIZE " + list.size());
        binding.contactRc.setAdapter(adapter);

        binding.add.setOnClickListener(v -> {
            if (binding.custom.getText().toString().isEmpty()) {
                Toast.makeText(this, "Number is Empty", Toast.LENGTH_SHORT).show();
            } else {
                list.add(new ContactModel("Custom Contact", binding.custom.getText().toString().trim()));
                Stash.put(Constants.CONTACTS, list);
                list.sort(Comparator.comparing(ContactModel::getContactName));
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notificationManager.isNotificationPolicyAccessGranted())
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (notificationManager.isNotificationPolicyAccessGranted())
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationManager.isNotificationPolicyAccessGranted())
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
    }

    private void askPermissions() {
        if (check()) {
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS);
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CALL_LOG);
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_PHONE_STATE);
            shouldShowRequestPermissionRationale(android.Manifest.permission.MODIFY_AUDIO_SETTINGS);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, 1222);
            }
        }
    }

    private boolean check() {
        return ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
    }

    private void selectAll() {
        list.clear();
        list = ContactManager.getAllContacts(this);
        list.sort(Comparator.comparing(ContactModel::getContactName));
        adapter = new ContactsAdapter(this, list);
        Log.d("CHECK123", "SIZE " + list.size());
        binding.contactRc.setAdapter(adapter);
        Stash.put(Constants.CONTACTS, list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if user granted access else ask for permission
                if (notificationManager.isNotificationPolicyAccessGranted()) {
                    getContact();
                } else {
                    // Open Setting screen to ask for permisssion
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, 1222);
                }
            } else {
                Toast.makeText(this, "Permission is required to get the Contact Details", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // if user granted access else ask for permission
                if (notificationManager.isNotificationPolicyAccessGranted()) {
                    selectAll();
                } else {
                    // Open Setting screen to ask for permisssion
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, 1222);
                }
            } else {
                Toast.makeText(this, "Permission is required to get the Contact Details", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getContact() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String contactName = cursor.getString(nameIndex);
                        String contactNumber = cursor.getString(phoneIndex);
                        list = Stash.getArrayList(Constants.CONTACTS, ContactModel.class);
                        list.add(new ContactModel(contactName, contactNumber));
                        Log.d("CHECK123", "SIZE " + list.size());
                        list.sort(Comparator.comparing(ContactModel::getContactName));
                        Stash.put(Constants.CONTACTS, list);
                        adapter = new ContactsAdapter(this, list);
                        binding.contactRc.setAdapter(adapter);
                    } else {
                        Log.d("CHECK123", "Empty");
                    }
                    cursor.close();
                }
            }
        }
    }

}