package blue.happening;


interface IHappeningCallback {
    void onClientAdded(String client);
    void onClientUpdated(String client);
    void onClientRemoved(String client);
    void onParcelQueued(long parcelId);
    void onMessageReceived(in byte[] message, int deviceId);
}
