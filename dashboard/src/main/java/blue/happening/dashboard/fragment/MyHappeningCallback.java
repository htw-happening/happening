package blue.happening.dashboard.fragment;

import android.os.IBinder;
import android.util.Log;

import blue.happening.sdk.HappeningCallback;
import blue.happening.HappeningClient;

/**
 * Created by kaischulz on 09.05.17.
 */

public class MyHappeningCallback implements HappeningCallback {

    @Override
    public void onClientAdded(HappeningClient client) {
        Log.i("MyHappeningCallback", "onClientAdded");
    }

    @Override
    public void onClientUpdated(HappeningClient client) {
        Log.i("MyHappeningCallback", "onClientUpdated");
    }

    @Override
    public void onClientRemoved(HappeningClient client) {
        Log.i("MyHappeningCallback", "onClientRemoved");
    }

    @Override
    public void onParcelQueued(long parcelId) {
        Log.i("MyHappeningCallback", "onParcelQueued");
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
