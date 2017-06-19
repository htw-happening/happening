package blue.happening.mesh;

public interface IMeshHandlerCallback {

    void onMessageReceived(byte[] message);

    void onDeviceAdded(MeshDevice meshDevice);

    void onDeviceUpdated(MeshDevice meshDevice);

    void onDeviceRemoved(MeshDevice meshDevice);
}
