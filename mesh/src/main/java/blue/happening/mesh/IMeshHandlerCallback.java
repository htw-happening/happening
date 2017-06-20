package blue.happening.mesh;


public interface IMeshHandlerCallback {

    void onDeviceAdded(MeshDevice meshDevice);

    void onDeviceUpdated(MeshDevice meshDevice);

    void onDeviceRemoved(MeshDevice meshDevice);

    void onMessageReceived(byte[] message, MeshDevice source);
}
