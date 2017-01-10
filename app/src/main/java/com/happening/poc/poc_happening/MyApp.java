package com.happening.poc.poc_happening;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static Context mContext;
    private static Boolean inForeground = false;

    public static Context getAppContext() {
        return mContext;
    }

    public static Boolean appInForeground() {
        return inForeground;
    }

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        inForeground = true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        inForeground = true;

    }

    @Override
    public void onActivityResumed(Activity activity) {
        inForeground = true;

    }

    @Override
    public void onActivityPaused(Activity activity) {
        inForeground = false;

    }

    @Override
    public void onActivityStopped(Activity activity) {
        inForeground = false;

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        inForeground = false;
    }

}
