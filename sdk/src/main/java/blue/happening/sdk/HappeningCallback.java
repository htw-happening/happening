package blue.happening.sdk;


public interface HappeningCallback {
    void onClientAdded(String client);

    void onClientUpdated(String client);

    void onClientRemoved(String client);

    void onParcelQueued(long parcelId);

    void onMessageReceived(byte[] message, int deviceId);

}
