package com.happening.poc.poc_happening;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by kaischulz on 13.12.16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";

    public AlarmReceiver() {
        Log.d(this.getClass().getSimpleName(), "constructor");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, TutorialService.class);
        context.startService(background);
    }

}