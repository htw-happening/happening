package blue.happening.sdk;

import blue.happening.HappeningClient;


public interface HappeningCallback {
    void onClientAdded(HappeningClient client);
    void onClientUpdated(HappeningClient client);
    void onClientRemoved(HappeningClient client);
    void onMessageReceived(byte[] message, HappeningClient source);
}
