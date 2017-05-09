package blue.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import blue.happening.IHappeningCallback;
import blue.happening.IHappeningService;

/**
 * Entry point for your application into the happening mesh network. You need to register and
 * deregister your {@link android.content.Context application context} during start and destroy
 * routines, respectively.
 */
public class Happening {

    private Context context;
    private RemoteServiceConnection remoteServiceConnection;
    private IHappeningService service;

    /**
     * To be able to send and receive data through the happening network service, you need to
     * register your application.
     *
     * @param context Your application context
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
     * To ensure a clean disconnect from the happening network, your application must be
     * deregistered during its destroy routine.
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

    public void registerHappeningCallback(HappeningCallback myClientCallback) {
        try {
            service.registerHappeningCallback((IHappeningCallback) myClientCallback);
        } catch (Exception exception) {

        }
    }

    /**
     * Dummy method to demonstrate how to communicate with the happening service.
     *
     * @param message A dummy message
     * @return error string
     */
    public String hello(String message) {
        Log.v(this.getClass().getSimpleName(), "hello");
        try {
            return service.hello(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            return "no reply";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "no service";
        }
    }

    /**
     * Service connection class that wraps the remote service interfaces.
     */
    private class RemoteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IHappeningService.Stub.asInterface(boundService);
            Log.i(this.getClass().getSimpleName(), "Service connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.i(this.getClass().getSimpleName(), "Service disconnected");
        }
    }

}
