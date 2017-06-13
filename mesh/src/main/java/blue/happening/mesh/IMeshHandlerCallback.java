package blue.happening.mesh;

public interface IMeshHandlerCallback {

    void onMessageReceived(byte[] message);

    void onDeviceAdded(String uuid);

    void onDeviceRemoved(String uuid);
}
