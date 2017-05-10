package blue.happening.dashboard.fragment;

import android.util.Log;

import blue.happening.sdk.HappeningCallback;


public class MyHappeningCallback implements HappeningCallback {

    @Override
    public void onClientAdded(String client) {
        Log.i("MyHappeningCallback", "onClientAdded " + client);
    }

    @Override
    public void onClientUpdated(String client) {
        Log.i("MyHappeningCallback", "onClientUpdated " + client);
    }

    @Override
    public void onClientRemoved(String client) {
        Log.i("MyHappeningCallback", "onClientRemoved " + client);
    }

    @Override
    public void onParcelQueued(long parcelId) {
        Log.i("MyHappeningCallback", "onParcelQueued");
    }
}
