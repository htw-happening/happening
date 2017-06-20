package blue.happening.simulation.entities;

import blue.happening.mesh.Layer;
import blue.happening.mesh.Message;


public class MockLayer extends Layer {

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

    void sendMessage(Message message) {
        byte[] bytes = message.toBytes();
        if (Math.random() <= 0.5) {
            getLayerCallback().onMessageReceived(bytes);
        }
    }
}
