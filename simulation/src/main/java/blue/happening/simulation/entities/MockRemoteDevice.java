package blue.happening.simulation.entities;

import java.util.ConcurrentModificationException;

import blue.happening.mesh.Message;
import blue.happening.mesh.RemoteDevice;
import blue.happening.simulation.demo.HappeningDemo;


public class MockRemoteDevice extends RemoteDevice {

    private Device device;

    public MockRemoteDevice(String uuid) {
        super(uuid);
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean sendMessage(Message message) {
        return sendMessageWithRetries(message, 2);
    }

    private boolean sendMessageWithRetries(Message message, int retries) {
        try {
            for (Connection connection : HappeningDemo.getGraph().getEdges()) {
                if (connection.getToDevice().getName().equals(getUuid()) &&
                        connection.getFromDevice().getName().equals(message.getPreviousHop())) {
                    connection.queueMessage(message);
                    return true;
                }
            }
        } catch (ConcurrentModificationException e) {
            if (retries > 0) {
                return sendMessageWithRetries(message, --retries);
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean remove() {
        return HappeningDemo.getGraph().removeVertex(device);
    }
}
