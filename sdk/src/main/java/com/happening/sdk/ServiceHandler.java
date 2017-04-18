package com.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.happening.HappeningInterface;
import com.happening.ServiceCallbackInterface;
import com.happening.service.HappeningService;

public class ServiceHandler {

    private static ServiceHandler sh = null;
    private RemoteServiceConnection serviceConnection;
    private HappeningInterface service;
    private ClientCallbackInterface onClientDiscoverCallback = null;

    private ServiceHandler() {
    }

    public static ServiceHandler getInstance() {
        if (sh == null)
            sh = new ServiceHandler();
        return sh;
    }

    private Context getContext() {
        return HappeningClient.getHappeningClient().getAppContext();
    }

    /**
     * This is our function which binds our activity(MainActivity) to our service(AddService).
     */
    private void initService() {
        serviceConnection = new RemoteServiceConnection();
        Intent i = new Intent(this.getContext(), HappeningService.class);
        i.setPackage("com.happening.happening_service");
        this.getContext().startService(i);
        boolean ret = this.getContext().bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().getSimpleName(), "initService() bound value: " + ret);
    }

    /**
     * This is our function to un-binds this activity from our service.
     */
    private void releaseService() {
        if (serviceConnection != null) {
            getContext().unbindService(serviceConnection);
            serviceConnection = null;
            service = null;
            Log.d(this.getClass().getSimpleName(), "releaseService(): unbound.");
        }
    }

    public void startService() {
        if (!isRunning())
            Log.d("jojo", "INIT start");
        initService();
    }

    public void stopService() {
        if (isRunning())
            releaseService();
    }

    public Boolean isRunning() {
        return service != null;
    }

    public void startClientScan() {
        try {
            service.startClientScan(mCallback);
        } catch (RemoteException e) {
        }
    }

    public void stopClientScan() {
        try {
            service.stopClientScan();
        } catch (RemoteException e) {
        }
    }

    public void registerDeviceDiscover(ClientCallbackInterface callback) {
        //TODO change onClientDiscoverCallback to array
        this.onClientDiscoverCallback = callback;
    }

    private ServiceCallbackInterface.Stub mCallback = new ServiceCallbackInterface.Stub() {
        public void onClientDiscovered(String name) throws RemoteException {
            onClientDiscoverCallback.onClientDiscovered(name);
        }
    };

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = HappeningInterface.Stub.asInterface((IBinder) boundService);
            Toast.makeText(HappeningClient.getHappeningClient().getAppContext(), "Service connected", Toast.LENGTH_LONG).show();
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Toast.makeText(HappeningClient.getHappeningClient().getAppContext(), "Service disconnected", Toast.LENGTH_LONG).show();
        }
    }

}
