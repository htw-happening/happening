package blue.happening.service.bluetooth;

/**
 * Created by fabi on 23.05.17.
 */

public interface ILayerCallback {
    public void onDeviceRemoved(IRemoteDevice device);
    public void onDeviceAdded(IRemoteDevice device);
    public void onReceivedMessage(byte[] bytes, IRemoteDevice device);
}
