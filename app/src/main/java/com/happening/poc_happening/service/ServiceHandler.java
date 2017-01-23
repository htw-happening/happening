package com.happening.poc_happening.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.happening.lib.IRemoteDeviceService;
import com.happening.service.HappeningService;

import com.happening.poc_happening.MyApp;


public class ServiceHandler {

    private static ServiceHandler sh = null;
    private Context context = null;

    private IRemoteDeviceService service;
    private RemoteServiceConnection serviceConnection;

    private ServiceHandler() {
        this.context = MyApp.getAppContext();
    }

    public static ServiceHandler getInstance() {
        if (sh == null)
            sh = new ServiceHandler();
        return sh;
    }

    /**
     * This is our function which binds our activity(MainActivity) to our service(AddService).
     */
    private void initService() {
        serviceConnection = new RemoteServiceConnection();
        Intent i = new Intent(context, HappeningService.class);
        i.setPackage("com.happening.happening_service");
        this.context.startService(i);
        boolean ret = this.context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.d(this.getClass().getSimpleName(), "initService() bound value: " + ret);
    }

    /**
     * This is our function to un-binds this activity from our service.
     */
    private void releaseService() {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
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
        return service != null ? true : false;
    }

    public IRemoteDeviceService getService() {
        return service;
    }

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteDeviceService.Stub.asInterface((IBinder) boundService);
            Toast.makeText(MyApp.getAppContext(), "Service connected", Toast.LENGTH_LONG)
                    .show();
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Toast.makeText(MyApp.getAppContext(), "Service disconnected", Toast.LENGTH_LONG)
                    .show();
        }
    }

}
