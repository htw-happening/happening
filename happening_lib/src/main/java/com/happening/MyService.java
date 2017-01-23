package com.happening;

import android.app.Application;
import android.content.Context;

public class MyService extends Application {

    private static  MyService myService = null;
    private static Context context = null;

    public MyService() {
        getInstance();
    }

    public MyService getInstance() {
        if (myService == null)
            this.myService = new MyService();
            this.context = this.getApplicationContext();

        return myService;
    }

    public static Context getContext() {
        return context;
    }

}
