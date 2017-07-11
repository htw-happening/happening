package blue.happening.dashboard.logic;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import blue.happening.HappeningClient;
import blue.happening.dashboard.MyApplication;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

public class BlueDashboard {

    private static BlueDashboard instance = null;
    private String TAG = getClass().getSimpleName();
    private Happening happening = null;
    private List<BlueCallback> listeners = new ArrayList<>();
    private List<HappeningClient> devices = new ArrayList<>();

    private BlueDashboard() {
        happening = new Happening();
        happening.register(MyApplication.getAppContext(), new HappeningCallback() {
            @Override
            public void onClientAdded(HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onClientAdded");
                addClient(happeningClient);
                for (BlueCallback listener : listeners) {
                    listener.onClientAdded();
                }
            }

            @Override
            public void onClientUpdated(HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onClientUpdated");
                clientUpdated(happeningClient);
                for (BlueCallback listener : listeners) {
                    listener.onClientUpdate();
                }
            }

            @Override
            public void onClientRemoved(HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onClientRemoved");
                removeClient(happeningClient);
            }

            @Override
            public void logMessage(int packageType, int action) {

            }

            @Override
            public void onMessageReceived(final byte[] bytes, HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onMessageReceived");

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getAppContext(), "Ping received " + new String(bytes), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public static BlueDashboard getInstance() {
        if (instance == null) {
            instance = new BlueDashboard();
        }
        return instance;
    }

    public List<HappeningClient> getDevices() {
        return devices;
    }

    public Happening getHappening() {
        return happening;
    }

    public void register(BlueCallback blueCallback) {
        if (!listeners.contains(blueCallback)) {
            listeners.add(blueCallback);
        }
    }

    public void deregister(BlueCallback blueCallback) {
        if (listeners.contains(blueCallback)) {
            listeners.remove(blueCallback);
        }
    }

    private HappeningClient addClient(HappeningClient client) {
        for (HappeningClient happeningClient : devices) {
            if (happeningClient.getUuid().equals(client.getUuid())) {
                Log.d(TAG, "addClient: Was already in list");
                return null;
            }
        }
        HappeningClient device = new HappeningClient(client.getUuid(), client.getName());
        devices.add(device);
        Log.d(TAG, "add client " + devices.size());
        return device;
    }

    private void clientUpdated(HappeningClient happeningClient) {
        addClient(happeningClient);
    }

    private void removeClient(HappeningClient client) {
        for (HappeningClient happeningClient : devices) {
            if (happeningClient.getUuid().equals(client.getUuid())) {
                devices.remove(happeningClient);
                Log.d(TAG, "removeClient: removed a client");
                return;
            }
        }
        Log.d(TAG, "removeClient: couldnt find that client - NOT GOOD");
    }

}
