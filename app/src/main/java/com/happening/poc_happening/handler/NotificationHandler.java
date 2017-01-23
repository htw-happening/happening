package com.happening.poc_happening.handler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.happening.poc_happening.MainActivity;
import com.happening.poc_happening.MyApp;
import com.happening.poc_happening.R;

public class NotificationHandler {

    private static NotificationHandler instance = null;

    private NotificationHandler() {

    }

    public static NotificationHandler getInstance() {
        if (instance == null)
            instance = new NotificationHandler();
        return instance;
    }

    public void doNotification(String title, String message) {
        Log.d(this.getClass().getSimpleName(), "show Notification " + title);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MyApp.getAppContext())
                        .setSmallIcon(R.drawable.side_nav_bar)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(2)
                        .setVibrate(new long[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2});
        Intent resultIntent = new Intent(MyApp.getAppContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyApp.getAppContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) MyApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(47474747, mBuilder.build());
    }
}
