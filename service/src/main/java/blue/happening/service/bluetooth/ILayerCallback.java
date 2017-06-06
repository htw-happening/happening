package blue.happening.service.bluetooth;

public interface ILayerCallback {
    public void onDeviceRemoved(IRemoteDevice device);
    public void onDeviceAdded(IRemoteDevice device);
    public void onMessageReceived(byte[] bytes);
}
