package blue.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class ServiceHandler implements IRemoteService {

    private Context context;
    private String appIdentifier;
    private RemoteServiceConnection remoteServiceConnection;

    private ServiceHandler(Context context, RemoteServiceConnection remoteServiceConnection) {
        this.context = context;
        this.appIdentifier = context.getApplicationInfo().processName;
        this.remoteServiceConnection = remoteServiceConnection;
    }

    public static ServiceHandler register(Context context) {
        RemoteServiceConnection remoteServiceConnection = new RemoteServiceConnection();
        Intent intent = new Intent("blue.happening.service.HappeningService");
        intent.setPackage("blue.happening.service");
        context.startService(intent);
        context.bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d("ServiceHandler", "Service bound to " + context.getPackageName());
        return new ServiceHandler(context, remoteServiceConnection);
    }

    public void deregister() {
        if (remoteServiceConnection != null) {
            context.unbindService(remoteServiceConnection);
            remoteServiceConnection = null;
            Log.i("ServiceHandler", "Service unbound from " + appIdentifier);
        } else {
            Log.i("ServiceHandler", "No service to unbind from " + appIdentifier);
        }
    }

    @Override
    public IBinder asBinder() {
        return (IBinder) remoteServiceConnection.getService();
    }

    @Override
    public void addDevice(String name) throws RemoteException {

    }

    static class RemoteServiceConnection implements ServiceConnection {

        private IRemoteService service;

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteService.Stub.asInterface((IBinder) boundService);
            Log.i("RemoteServiceConnection", "Service connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.i("RemoteServiceConnection", "Service disconnected");
        }

        public IRemoteService getService() {
            return service;
        }
    }

}