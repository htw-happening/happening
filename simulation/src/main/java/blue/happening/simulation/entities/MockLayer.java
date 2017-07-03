package blue.happening.simulation.entities;

import blue.happening.mesh.Layer;


public class MockLayer extends Layer {

    private float messageLoss = 0;

    void addDevice(Device device) {
        MockRemoteDevice remoteDevice = new MockRemoteDevice(device.getName());
        remoteDevice.setDevice(device);
        getLayerCallback().onDeviceAdded(remoteDevice);
    }

    void removeDevice(Device device) {
        MockRemoteDevice remoteDevice = new MockRemoteDevice(device.getName());
        remoteDevice.setDevice(device);
        getLayerCallback().onDeviceRemoved(remoteDevice);
    }

    public float getMessageLoss() {
        return messageLoss;
    }

    public void setMessageLoss(float messageLoss) {
        this.messageLoss = messageLoss;
    }
}
