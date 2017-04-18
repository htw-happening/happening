package com.happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.happening.HappeningInterface;
import com.happening.MyService;
import com.happening.ServiceCallbackInterface;
import com.happening.bluetooth.Layer;

public class HappeningService extends Service {

    private ServiceCallbackInterface deviceDiscoveredCallback = null;

    private final HappeningInterface.Stub mBinder = new HappeningInterface.Stub() {
        @Override
        public void startClientScan(ServiceCallbackInterface callback) throws RemoteException {
            Layer.getInstance().addHandler(guiHandler);
            Layer.getInstance().startAdvertising();
            Layer.getInstance().startScan();
            deviceDiscoveredCallback = callback;
        }

        @Override
        public void stopClientScan() throws RemoteException {
        }
    };

    private Handler guiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Layer.DEVICE_POOL_UPDATED:
                    try {
                        deviceDiscoveredCallback.onClientDiscovered(Layer.getInstance().getDevicePool().toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case Layer.MESSAGE_RECEIVED:
                    break;
                default:
                    Log.i("HANDLER", "Unresolved Message Code");
                    break;
            }
        }
    };

    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode = START_STICKY;
    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind = true;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate " + this.toString());
        MyService.setContext(getApplicationContext());
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
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onRebind");
    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.d(this.getClass().getSimpleName(), "onDestroy");
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(this.getClass().getSimpleName(), "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

}
