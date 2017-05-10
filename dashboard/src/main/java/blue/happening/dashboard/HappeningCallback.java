package blue.happening.dashboard;

import android.util.Log;


public class HappeningCallback implements blue.happening.sdk.HappeningCallback {

    @Override
    public void onClientAdded(String client) {
        Log.i("HappeningCallback", "onClientAdded " + client);
    }

    @Override
    public void onClientUpdated(String client) {
        Log.i("HappeningCallback", "onClientUpdated " + client);
    }

    @Override
    public void onClientRemoved(String client) {
        Log.i("HappeningCallback", "onClientRemoved " + client);
    }

    @Override
    public void onParcelQueued(long parcelId) {
        Log.i("HappeningCallback", "onParcelQueued");
    }
}
