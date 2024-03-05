package com.moutamid.callmanager.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fxn.stash.Stash;
import com.moutamid.callmanager.Constants;
import com.moutamid.callmanager.models.ContactModel;

import java.util.ArrayList;

public class MissedCallReceiver extends BroadcastReceiver {
    String TAG = "MyPhoneStateListener";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        this.context = context;
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (Stash.getBoolean("key", false)) {
            return;
        }

        if (Stash.getBoolean("activate", false)) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d(TAG, "onReceive: numberrr  " + number);
                minAudio();
                if (number != null) {
                    Log.d(TAG, "onReceive: isWithinTimeWindow  " + isWithinTimeWindow(number));
                    if (isWithinTimeWindow(number)) {
                        maxAudio();
                        new Handler().postDelayed(this::minAudio, 40000);
                    } else {
                        minAudio();
                    }
                }
            }
        }
    }

    private void minAudio() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void maxAudio() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          //  audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING), 0);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        } else {
          //  audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    private boolean isWithinTimeWindow(String incomingNumber) {
        if (!incomingNumber.isEmpty()) {
            ArrayList<ContactModel> list = Stash.getArrayList(Constants.CONTACTS, ContactModel.class);
            for (ContactModel model : list) {
                if (model.getContactNumber().replace(" ", "").contains(incomingNumber.replace(" ", ""))) {
                    return true;
                }
            }
        }
        return false;
    }

}