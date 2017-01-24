package com.happening;

import android.app.Application;
import android.content.Context;

public class MyService extends Application {

    private static Context context = null;

    public MyService() {
        this.context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
