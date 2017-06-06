package blue.happening.mesh;

public interface IMeshHandlerCallback {

    void onMessageReceived(String message);

    void onDeviceAdded(String uuid);

    void onDeviceRemoved(String uuid);
}
