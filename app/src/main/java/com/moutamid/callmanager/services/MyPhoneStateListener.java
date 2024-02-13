package com.moutamid.callmanager.services;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fxn.stash.Stash;
import com.moutamid.callmanager.Constants;
import com.moutamid.callmanager.models.ContactModel;

import java.util.ArrayList;

public class MyPhoneStateListener extends PhoneStateListener {
    private Context context;
    String TAG = "MyPhoneStateListener";
    int i = 0;
    int lastState = TelephonyManager.CALL_STATE_IDLE;

    public MyPhoneStateListener(Context context) {
        this.context = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        Log.d(TAG, "onCallStateChanged: STATE " + state);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
            case TelephonyManager.CALL_STATE_OFFHOOK:
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d(TAG, "onCallStateChanged: " + incomingNumber);
                Log.d(TAG, "BOOOLLL : " + isWithinTimeWindow(incomingNumber));
                if (isWithinTimeWindow(incomingNumber)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    } else {
                        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC), 0);
                    } else {
                        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    }
                }
                break;
        }

    }

    private boolean isWithinTimeWindow(String incomingNumber) {
        ArrayList<ContactModel> list = Stash.getArrayList(Constants.CONTACTS, ContactModel.class);
        for (ContactModel model : list) {
            if (model.getContactNumber().contains(incomingNumber)) {
                return true;
            }
        }
        return false;
    }

}
