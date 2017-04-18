package blue.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import blue.happening.service.HappeningService;


public class ServiceHandler implements IRemoteHappening {

    private static ServiceHandler instance;
    private Context context;
    private RemoteServiceConnection serviceConnection;
    private IRemoteHappening service;

    private ServiceHandler() {
        context = null;
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

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public void addDevice(String name) throws RemoteException {

    }

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteHappening.Stub.asInterface((IBinder) boundService);
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }

}