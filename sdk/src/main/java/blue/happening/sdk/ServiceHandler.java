package blue.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


public class ServiceHandler {

    private Context context;
    private String appIdentifier = "";
    private RemoteServiceConnection remoteServiceConnection;
    private IRemoteService service;

    public IRemoteService register(Context context) {
        this.context = context;
        appIdentifier = context.getApplicationInfo().processName;
        remoteServiceConnection = new RemoteServiceConnection();
        Intent intent = new Intent("blue.happening.service.IRemoteService");
        intent.setPackage("blue.happening.service");
        // Intent intent = new Intent();
        // ComponentName componentName = new ComponentName("blue.happening.service", "blue.happening.service.HappeningService");
        // intent.setComponent(componentName);
        context.startService(intent);
        boolean success = context.bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
        Log.i("ServiceHandler", "Start success " + success);
        return service;  // XXX: Should be a service promise
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

    class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteService.Stub.asInterface((IBinder) boundService);
            Log.i("RemoteServiceConnection", "Service connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.i("RemoteServiceConnection", "Service disconnected");
        }
    }
}