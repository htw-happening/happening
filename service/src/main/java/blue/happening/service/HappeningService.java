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

        @Override
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

        @Override
        public BluetoothDevice getDevice(String name) throws RemoteException {
            for (BluetoothDevice device : devices) {
                if (device.getName().equalsIgnoreCase(name)) {
                    Log.d("get device", device.toString());
                    return device;
                }
            }
            return null;
        }

        @Override
        public List<BluetoothDevice> getDevices() throws RemoteException {
            Log.d("get device", String.valueOf(devices));
            return devices;
        }

        @Override
        public void enableAdapter() throws RemoteException {
            Layer.getInstance().enableAdapter();
        }

        @Override
        public void disableAdapter() throws RemoteException {
            Layer.getInstance().disableAdapter();
        }

        @Override
        public boolean isBtAdapterEnabled() throws RemoteException {
            return Layer.getInstance().isEnabled();
        }

        @Override
        public void startScan() throws RemoteException {
            Layer.getInstance().startScan();
        }

        @Override
        public void stopScan() throws RemoteException {
            Layer.getInstance().stopScan();
        }

        @Override
        public void startAdvertising() throws RemoteException {
            Layer.getInstance().startAdvertising();
        }

        @Override
        public void stopAdvertising() throws RemoteException {
            Layer.getInstance().stopAdvertising();
        }

        @Override
        public boolean isAdvertisingSupported() throws RemoteException {
            return Layer.getInstance().isAdvertisingSupported();
        }

        @Override
        public void createGattServer() throws RemoteException {
            Layer.getInstance().createGattServer();
        }

        @Override
        public void stopGattServer() throws RemoteException {
            Layer.getInstance().stopGattServer();
        }

//        @Override
//        public void addHandler(Handler handler) throws RemoteException{
//        }
//
//        @Override
//        public void removeHandler(Handler handler) throws RemoteException{
//        }

        @Override
        public void broadcastMessage(String message) throws RemoteException {
            Layer.getInstance().broadcastMessage(message);
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
