package com.moutamid.callmanager.services;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fxn.stash.Stash;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MissedCallReceiver extends BroadcastReceiver {
    String TAG = "MyPhoneStateListener";
    static MyPhoneStateListener listener;

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        this.context = context;

        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            Log.d(TAG, "onReceive CALL");
            if(listener == null){
                listener = new MyPhoneStateListener(context);
            }
            TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

}