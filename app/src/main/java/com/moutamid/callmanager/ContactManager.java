package com.moutamid.callmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.moutamid.callmanager.models.ContactModel;

import java.util.ArrayList;

public class ContactManager {
    public static ArrayList<ContactModel> getAllContacts(Context context) {
        ArrayList<ContactModel> contactList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int indexName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String contactName = cursor.getString(indexName);
                int indexNum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String contactNumber = cursor.getString(indexNum);
                contactList.add(new ContactModel(contactName, contactNumber));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return contactList;
    }
}
