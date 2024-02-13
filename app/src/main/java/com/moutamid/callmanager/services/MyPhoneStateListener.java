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
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                maxAudio();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                maxAudio();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                if (isWithinTimeWindow(incomingNumber)) {
                    maxAudio();
                } else {
                    minAudio();
                }
                break;
        }
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
                if (model.getContactNumber().contains(incomingNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

}
