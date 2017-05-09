package blue.happening.sdk;

/**
 * Created by kaischulz on 09.05.17.
 */

public interface HappeningCallback {
    void onClientAdded(String client);
    void onClientUpdated(String client);
    void onClientRemoved(String client);
    void onParcelQueued(long parcelId);
}
