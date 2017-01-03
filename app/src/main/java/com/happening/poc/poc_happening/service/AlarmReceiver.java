package com.happening.poc.poc_happening.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onReceive");

        Intent background = new Intent(context, Bluetooth4Service.class);
        context.startService(background);
    }

}