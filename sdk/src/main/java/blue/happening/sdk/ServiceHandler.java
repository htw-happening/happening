package blue.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 *
 */
public class ServiceHandler {

    private Context context;
    private RemoteServiceConnection remoteServiceConnection;
    private IRemoteService service;

    /**
     *
     * @param context
     */
    public void register(Context context) {
        this.context = context;
        remoteServiceConnection = new RemoteServiceConnection();
        Intent intent = new Intent();
        intent.setClassName("blue.happening.service", "blue.happening.service.HappeningService");
        context.startService(intent);
        boolean success = context.bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE);
        Log.i(this.getClass().getSimpleName(), "start success " + success);
    }

    /**
     *
     */
    public void deregister() {
        if (remoteServiceConnection != null) {
            context.unbindService(remoteServiceConnection);
            remoteServiceConnection = null;
            Log.i(this.getClass().getSimpleName(), "service unbound");
        } else {
            Log.i(this.getClass().getSimpleName(), "no service to unbind from");
        }
    }

    /**
     * @return
     */
    public IRemoteService getService() {
        return service;
    }

    /**
     *
     */
    private class RemoteServiceConnection implements ServiceConnection {

        /**
         * @param name
         * @param boundService
         */
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IRemoteService.Stub.asInterface(boundService);
            Log.i(this.getClass().getSimpleName(), "Service connected");
        }

        /**
         * @param name
         */
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.i(this.getClass().getSimpleName(), "Service disconnected");
        }
    }
}