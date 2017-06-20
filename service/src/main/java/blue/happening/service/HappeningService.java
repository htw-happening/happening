package blue.happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;
import blue.happening.IHappeningService;
import blue.happening.mesh.IMeshHandlerCallback;
import blue.happening.mesh.MeshHandler;
import blue.happening.service.bluetooth.AppPackage;
import blue.happening.service.bluetooth.Layer;


/**
 * Main happening {@link Service service} class containing lifecycle management and
 * an interface binding with the actual service methods.
 */
public class HappeningService extends Service {

    private static final String HAPPENING_APP_ID = "HAPPENING_APP_ID";
    private static final int START_MODE = START_STICKY;
    private static final boolean ALLOW_REBIND = true;

    private static HashMap<String, IHappeningCallback> callbacks = new HashMap<>();
    private final String TAG = getClass().getSimpleName();
    private Layer bluetoothLayer = null;
    private MeshHandler meshHandler = null;
    private final IHappeningService.Stub binder = new IHappeningService.Stub() {

        @Override
        public void registerHappeningCallback(IHappeningCallback happeningCallback, String appId) throws RemoteException {
            Log.d(TAG, "callback added " + happeningCallback);
            callbacks.put(appId, happeningCallback);
        }

        @Override
        public List<HappeningClient> getDevices() throws RemoteException {
            Log.v(this.getClass().getSimpleName(), "getDevices");

            List<String> deviceKeys = meshHandler.getDevices();

            System.out.println("getDevices Num of real direct connections " + bluetoothLayer.getNumOfConnectedDevices());
            System.out.println("getDevices: size " + deviceKeys.size());

            List<HappeningClient> devices = new ArrayList<>();
            for (String deviceKey : deviceKeys) {
                devices.add(new HappeningClient(deviceKey, "N/A"));
                Log.d(TAG, "getDevices: " + deviceKey);
            }

            return devices;
        }

        @Override
        public void sendToDevice(String deviceId, String appId, byte[] content) throws RemoteException {
            System.out.println(TAG + " " + "sendToDevice: " + new String(content));
            Log.v(this.getClass().getSimpleName(), "sendToDevice");
            // TODO: Meshhandler.sendId(deviceId, content)
            byte[] data = AppPackage.createAppPackage(appId.hashCode(), content);
            meshHandler.sendMessage(deviceId, data);
//            Layer.getInstance().sendToDevice(deviceId, content);
        }

        @Override
        public void restart() throws RemoteException {
            Log.v(this.getClass().getSimpleName(), "restart");
            Layer.getInstance().reset();
        }
    };

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(this.getClass().getSimpleName(), "onCreate");

        bluetoothLayer = Layer.getInstance();
        meshHandler = new MeshHandler(bluetoothLayer.getMacAddress());
        meshHandler.registerLayer(bluetoothLayer);
        meshHandler.registerCallback(new IMeshHandlerCallback() {
            @Override
            public void onMessageReceived(byte[] message) {
                int appId = AppPackage.getAppID(message);
                byte[] bytes = AppPackage.getContent(message);
                String content = new String(bytes);
                System.out.println("ON MESSAGE RECEIVED!!!");
                System.out.println("ON MESSAGE RECEIVED!!! " + content);

                for (Map.Entry<String, IHappeningCallback> entry : callbacks.entrySet()) {
                    if (entry.getKey().hashCode() == appId) {
                        try {
                            System.out.println("ON MESSAGE RECEIVED!!! " + "delivered " + content + " " + appId);
                            entry.getValue().onMessageReceived(bytes, appId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onDeviceAdded(String uuid) {
                System.out.println("DISCO DISCOVERED: " + uuid);
//                meshMembers.add(uuid);
            }

            @Override
            public void onDeviceRemoved(String uuid) {
//                meshMembers.remove(uuid);
            }
        });

        bluetoothLayer.start();
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(this.getClass().getSimpleName(), "onStartCommand");
        Intent intents = new Intent(getBaseContext(), MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
        Toast.makeText(this, "Happening started", Toast.LENGTH_SHORT).show();
        return START_MODE;
    }


    /**
     * Called when the service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        Toast.makeText(this, "Happening stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(this.getClass().getSimpleName(), "onBind");
        String appId = intent.getStringExtra(HAPPENING_APP_ID);
        Toast.makeText(this, (appId == null ? "Something" : appId) + " bound", Toast.LENGTH_LONG).show();
        return binder;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        String appId = intent.getStringExtra(HAPPENING_APP_ID);
        Toast.makeText(this, (appId == null ? "Something" : appId) + " rebound", Toast.LENGTH_LONG).show();
        Log.v(this.getClass().getSimpleName(), "onRebind");
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(this.getClass().getSimpleName(), "onUnbind");
        String appId = intent.getStringExtra(HAPPENING_APP_ID);
        Toast.makeText(this, (appId == null ? "Something" : appId) + " unbound", Toast.LENGTH_LONG).show();
        return ALLOW_REBIND;
    }

}
