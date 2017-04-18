package blue.happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import blue.happening.IRemoteHappening;
import blue.happening.bluetooth.Layer;
import blue.happening.lib.BluetoothDevice;


public class HappeningService extends Service {

    private final IRemoteHappening.Stub binder = new IRemoteHappening.Stub() {

        List<BluetoothDevice> devices = Collections.synchronizedList(new ArrayList<BluetoothDevice>());

        public void addDevice(String name) throws RemoteException {

            getApplicationContext();
            // check if device is already in device list
            for (BluetoothDevice d : devices) {
                Log.d("name", d.getName());
                if (d.getName().equals(name)) {
                    Log.d("already in list", name);
                    return;
                }
            }

            // add if not in device list
            BluetoothDevice device = new BluetoothDevice(name);
            devices.add(device);
            Log.d("add device", name);

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
        return binder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(this.getClass().getSimpleName(), "onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

}
