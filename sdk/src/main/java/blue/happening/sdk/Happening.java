package blue.happening.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;
import blue.happening.IHappeningService;

import static android.content.ContentValues.TAG;


/**
 * Entry point for your application into the happening mesh network. You need to register and
 * deregister your {@link android.content.Context application context} during start and destroy
 * routines, respectively.
 */
public class Happening {

    private static final String HAPPENING_APP_ID = "HAPPENING_APP_ID";
    private static String appId = null;

    private Context context;
    private Handler handler = new Handler();
    private IHappeningService service;
    private HappeningCallback appCallback;
    private IHappeningCallback.Stub happeningCallback = new IHappeningCallback.Stub() {

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public void onClientAdded(HappeningClient client) throws RemoteException {
            appCallback.onClientAdded(client);
        }

        @Override
        public void onClientUpdated(HappeningClient client) throws RemoteException {
            appCallback.onClientUpdated(client);
        }

        @Override
        public void onClientRemoved(HappeningClient client) throws RemoteException {
            appCallback.onClientRemoved(client);
        }

        @Override
        public void onMessageReceived(byte[] message, HappeningClient source) throws RemoteException {
            appCallback.onMessageReceived(message, source);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IHappeningService.Stub.asInterface(boundService);
            try {
                service.registerHappeningCallback(happeningCallback, appId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(this.getClass().getSimpleName(), "Service connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            handler.postDelayed(runnable, 10);
            Log.i(this.getClass().getSimpleName(), "Service disconnected");
        }
    };

    private final Runnable runnable = new Runnable() {
        public void run() {
            if (service == null) {
                boolean success = bindService();
                Log.i(this.getClass().getSimpleName(), "Restart success " + success);
                handler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * To be able to send and receive data through the happening network service, you need to
     * register your application.
     *
     * @param context Your application context
     */
    public void register(@NonNull Context context, @NonNull HappeningCallback appCallback) {
        this.context = context;
        this.appCallback = appCallback;

        ApplicationInfo info = context.getApplicationInfo();
        appId = info.labelRes == 0 ? info.nonLocalizedLabel.toString() : context.getString(info.labelRes);

        boolean success = bindService();
        Log.i(this.getClass().getSimpleName(), "Start success " + success);
    }

    /**
     * To ensure a clean disconnect from the happening network, your application must be
     * deregistered during its destroy routine.
     */
    public void deregister() {
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
            Log.i(this.getClass().getSimpleName(), "service unbound");
        } else {
            Log.i(this.getClass().getSimpleName(), "no service to unbind from");
        }
    }

    /**
     * Internal method for re/binding to a running HappeningService.
     *
     * @return Whether or not the binding has been kicked off successfully.
     */
    private boolean bindService() {
        Intent intent = new Intent();
        intent.setClassName("blue.happening.service", "blue.happening.service.HappeningService");
        ApplicationInfo info = context.getApplicationInfo();
        String appId = info.labelRes == 0 ? info.nonLocalizedLabel.toString() : context.getString(info.labelRes);
        intent.putExtra(HAPPENING_APP_ID, appId);
        Log.d(TAG, "bindService: " + appId);
        context.startService(intent);
        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Method to get List of all connected Devices.
     *
     * @return List of {@link HappeningClient happening clients}
     */
    public List<HappeningClient> getClients() {
        Log.v(this.getClass().getSimpleName(), "getClients");
        List<HappeningClient> devicesList = null;
        try {
            devicesList = service.getClients();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
        if (devicesList == null) {
            devicesList = new ArrayList<>();
        }
        return devicesList;
    }

    /**
     * Method to send data to a specific device.
     */
    public void sendMessage(byte[] message, HappeningClient destination) {
        try {
            service.sendMessage(message, destination.getUuid(), appId);
            Log.d(TAG, "sendMessage: " + destination.getUuid() + " with appId " + appId);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restart the happening Service.
     * In case of Emergency - Use this Method when nothing works :-)
     */
    public void restartHappeningService() {
        Log.v(this.getClass().getSimpleName(), "restartHappeningService");
        try {
            service.restart();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

}
