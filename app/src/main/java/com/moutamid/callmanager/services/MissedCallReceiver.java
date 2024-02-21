package com.moutamid.callmanager.services;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.moutamid.callmanager.Constants;
import com.moutamid.callmanager.models.ContactModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "onReceive: numberrr  " + number);
            minAudio();
            if (number != null){
                if (isWithinTimeWindow(number)) {
                    maxAudio();
                } else {
                    minAudio();
                }
            }

        }

/*        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            Log.d(TAG, "onReceive CALL");
            if(listener == null){
                listener = new MyPhoneStateListener(context);
            }
            TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }*/
    }

    private void minAudio() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMinVolume(AudioManager.STREAM_RING), 0);
        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    private void maxAudio() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    private boolean isWithinTimeWindow(String incomingNumber) {
        if (!incomingNumber.isEmpty()){
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