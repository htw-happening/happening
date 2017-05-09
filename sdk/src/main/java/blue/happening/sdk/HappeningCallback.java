package blue.happening.sdk;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;

/**
 * Created by kaischulz on 09.05.17.
 */

public interface HappeningCallback extends IHappeningCallback {
    void onClientAdded(HappeningClient client);
    void onClientUpdated(HappeningClient client);
    void onClientRemoved(HappeningClient client);
    void onParcelQueued(long parcelId);
}
