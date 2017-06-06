package blue.happening.mesh;

public interface ILayerCallback {

    public void onDeviceAdded(RemoteDevice remoteDevice);

    public void onDeviceRemoved(RemoteDevice remoteDevice);

    public void onMessageReceived(byte[] bytes);

}
