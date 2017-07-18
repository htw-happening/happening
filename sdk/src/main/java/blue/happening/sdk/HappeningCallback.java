package blue.happening.sdk;

import blue.happening.HappeningClient;

public interface HappeningCallback {

    int MESSAGE_ACTION_ARRIVED = 0;
    int MESSAGE_ACTION_RECEIVED = 1;
    int MESSAGE_ACTION_DROPPED = 2;
    int MESSAGE_ACTION_FORWARDED = 3;
    int MESSAGE_ACTION_SENT = 4;

    int MESSAGE_TYPE_OGM = 1;
    int MESSAGE_TYPE_UCM = 2;

    void onClientAdded(HappeningClient client);
    void onClientUpdated(HappeningClient client);
    void onClientRemoved(HappeningClient client);
    void onMessageLogged(int packageType, int action);
    void onMessageReceived(byte[] message, HappeningClient source);
}
