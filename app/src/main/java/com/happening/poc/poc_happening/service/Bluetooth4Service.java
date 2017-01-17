package com.happening.poc.poc_happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.happening.bt.service.BtService;
import com.happening.poc.poc_happening.MyApp;
import com.happening.poc.poc_happening.bluetooth.Layer;
import com.happening.poc.poc_happening.handler.NotificationHandler;

public class Bluetooth4Service extends Service {

    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode = START_STICKY;

    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind = true;

    private Layer bt4Layer = null;

    private Handler backgroundServiceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            NotificationHandler.getInstance().doNotification("Happening", "This is an awseom Notification...");
        }
    };

    private Handler foregroundServiceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //TODO Do the magic for GUI
        }
    };

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate " + this.toString());

        bt4Layer = Layer.getInstance(this);
        registerHandler();

        bt4Layer.startAdvertising();
        bt4Layer.startScan();

    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this.getClass().getSimpleName(), "onStartCommand");
        return mStartMode;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onUnbind");
        bt4Layer.stopAdvertising();
        bt4Layer.stopScan();
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onRebind");
        bt4Layer.startAdvertising();
        bt4Layer.startScan();
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.d(this.getClass().getSimpleName(), "onDestroy");
        bt4Layer.stopAdvertising();
        bt4Layer.stopScan();
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {

        return new BtService.Stub() {
            /**
             * In the AIDL file we just add the declaration of the function
             * here is the real implementation of the add() function below
             */
            public int add(int ValueFirst, int valueSecond) throws RemoteException {
                Log.i("jojo", String.format("AddService.add(%d, %d)", ValueFirst, valueSecond));
                return (ValueFirst + valueSecond);
            }
        };

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(this.getClass().getSimpleName(), "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    public void registerHandler() {
//        Log.d("HANDLER", MyApp.appInForeground().toString());
//        Log.d("HANDLER", "" + System.identityHashCode(MyApp.class));

        if (MyApp.appInForeground()) {
            bt4Layer.addHandler(foregroundServiceHandler);
        } else {
            bt4Layer.addHandler(backgroundServiceHandler);
        }
    }

}
