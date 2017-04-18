package blue.happening.chat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import blue.happening.sdk.ServiceHandler;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static Context context;
    private static Boolean inForeground = false;
    private static ServiceHandler serviceHandler = null;

    public static Context getAppContext() {
        return context;
    }

    public static Boolean isInForeground() {
        return inForeground;
    }

    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate");
        context = getApplicationContext();
        registerActivityLifecycleCallbacks(this);
        this.serviceHandler = ServiceHandler.getInstance();
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
