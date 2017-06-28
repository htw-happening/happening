package blue.happening.dashboard.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import blue.happening.HappeningClient;
import blue.happening.dashboard.MyApplication;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

/**
 * Created by kaischulz on 28.06.17.
 */

public class BlueDashboard {

    private static BlueDashboard instance = null;
    private String TAG = getClass().getSimpleName();
    private Happening happening = null;
    private List<BlueCallback> listeners;
    private BlueDashboard() {
        happening = new Happening();
        happening.register(MyApplication.getAppContext(), new HappeningCallback() {
            @Override
            public void onClientAdded(HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onClientAdded");
                for (BlueCallback listener : listeners) {
                    listener.onClientUpdate();
                }
            }

            @Override
            public void onClientUpdated(HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onClientUpdated");
                for (BlueCallback listener : listeners) {
                    listener.onClientUpdate();
                }
            }

            @Override
            public void onClientRemoved(HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onClientRemoved");
            }

            @Override
            public void onMessageReceived(byte[] bytes, HappeningClient happeningClient) {
                Log.d(TAG, "HappeningCallback - onMessageReceived");
            }
        });

        listeners = new ArrayList<>();
    }

    public static BlueDashboard getInstance() {
        if (instance == null) {
            instance = new BlueDashboard();
        }
        return instance;
    }

    public Happening getHappening() {
        return happening;
    }

    public void register(BlueCallback blueCallback) {
        if (!listeners.contains(blueCallback)) {
            Log.d(TAG, "register: True");
            listeners.add(blueCallback);
        }
    }

    public void deregister(BlueCallback blueCallback) {
        if (listeners.contains(blueCallback)) {
            Log.d(TAG, "deregister: True");
            listeners.remove(blueCallback);
        }
    }

}
