package com.happening.poc.poc_happening;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {

    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
