package com.happening.poc_happening;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.happening.poc_happening.service.ServiceHandler;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static Context mContext;
    private static Boolean inForeground = false;
    private static ServiceHandler sh = null;

    public static Context getAppContext() {
        return mContext;
    }

    public static Boolean appInForeground() {
        return inForeground;
    }

    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate");
        mContext = getApplicationContext();
        registerActivityLifecycleCallbacks(this);

        // connect to process - happening_lib
        this.sh = ServiceHandler.getInstance();
        sh.startService();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onActivityCreated");
        inForeground = true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onActivityStarted");
        inForeground = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onActivityResumed");
        inForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onActivityPaused");
        inForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onActivityStopped");
        inForeground = false;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(this.getClass().getSimpleName(), "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(this.getClass().getSimpleName(), "onActivityDestroyed");
        inForeground = false;
    }

}
