package blue.happening.chat.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import blue.happening.IRemoteHappening;
import blue.happening.chat.MyApp;
import blue.happening.lib.BluetoothDevice;
import blue.happening.service.HappeningService;


public class ServiceHandler implements IRemoteHappening {

    private static ServiceHandler instance;
    private Context context;
    private RemoteServiceConnection serviceConnection;
    private IRemoteHappening service;

    private ServiceHandler() {
        context = MyApp.getAppContext();
    }

    public static ServiceHandler getInstance() {
        if (instance == null)
            instance = new ServiceHandler();
        return instance;
    }

    /**
     * Bind activity to our service
     */
    private void initService() {
        serviceConnection = new RemoteServiceConnection();
        Intent intent = new Intent(context, HappeningService.class);
        intent.setPackage("blue.happening.service");
        context.startService(intent);
        boolean ret = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d("ServiceHandler", "initService() bound value: " + ret);
    }

    /**
     * Release service from activity
     */
    private void releaseService() {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
            serviceConnection = null;
            service = null;
            Log.i("ServiceHandler", "Service released");
        }
    }

    public void startService() {
        if (!isRunning()) {
            initService();
            Log.i("ServiceHandler", "Service started");
        }
    }

    public void stopService() {
        if (isRunning()) {
            releaseService();
            Log.i("ServiceHandler", "Service started");
        }
    }

    public Boolean isRunning() {
        return service != null ? true : false;
    }

    public void addDevice(String name) {
        try {
            service.addDevice(name);
        } catch (RemoteException e) {
        }
    }

    public BluetoothDevice getDevice(String name) {
        try {
            return service.getDevice(name);
        } catch (RemoteException e) {
            return null;
        }
    }

    public List<BluetoothDevice> getDevices() {
        try {
            return service.getDevices();
        } catch (RemoteException e) {
            return null;
        }
    }

    public void enableAdapter() {
        try {
            service.enableAdapter();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void disableAdapter() {
        try {
            service.disableAdapter();
        } catch (RemoteException e) {
        }
    }

    public boolean isBtAdapterEnabled() {
        try {
            return service.isBtAdapterEnabled();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void startScan() {
        try {
            service.startScan();
        } catch (RemoteException e) {
        }
    }

    public void stopScan() {
        try {
            service.stopScan();
        } catch (RemoteException e) {
        }
    }

    public void startAdvertising() {
        try {
            service.startAdvertising();
        } catch (RemoteException e) {
        }
    }

    public void stopAdvertising() {
        try {
            service.stopAdvertising();
        } catch (RemoteException e) {
        }
    }

    public boolean isAdvertisingSupported() {
        try {
            return service.isAdvertisingSupported();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void createGattServer() {
        try {
            service.createGattServer();
        } catch (RemoteException e) {
        }
    }

    public void stopGattServer() {
        try {
            service.stopGattServer();
        } catch (RemoteException e) {
        }
    }

    public void broadcastMessage(String message) {
        try {
            service.broadcastMessage(message);
        } catch (RemoteException e) {
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteHappening.Stub.asInterface((IBinder) boundService);
            Toast.makeText(MyApp.getAppContext(), "Service connected", Toast.LENGTH_LONG).show();
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Toast.makeText(MyApp.getAppContext(), "Service disconnected", Toast.LENGTH_LONG).show();
        }
    }

}
