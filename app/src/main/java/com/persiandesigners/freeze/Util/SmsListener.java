package com.persiandesigners.freeze.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Navid on 1/7/2018.
 */

public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] smsMessage = new SmsMessage[pdus.length];
                    for (int n = 0; n < pdus.length; n++) {
                        smsMessage[n] = SmsMessage.createFromPdu((byte[]) pdus[n]);
                    }
                    String mainsms = "";
                    for (int i = 0; i < smsMessage.length; i++)
                        mainsms += smsMessage[i].getMessageBody();

                    if(mainsms.contains("سازی") || mainsms.contains("عبور") || mainsms.contains("کد")) {
                        String number = mainsms.replaceAll("\\D+", "");
                        Log.v("this", "code " + number);

                        SharedPreferences settings = context.getSharedPreferences("settings", context.MODE_PRIVATE);
                        SharedPreferences.Editor pref = settings.edit();
                        pref.putString("code", number);
                        pref.commit();
                    }
                } catch (Exception e) {
                    Log.v("Exception caught", e.getMessage());
                }
            }
        }
    }
}
