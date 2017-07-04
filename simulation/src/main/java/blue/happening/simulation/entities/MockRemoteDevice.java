package blue.happening.simulation.entities;

import blue.happening.mesh.Message;
import blue.happening.mesh.RemoteDevice;


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
        for (Object object : device.getNetworkGraph().getEdges()) {
            Connection connection = (Connection) object;
            if (connection.getToDevice().getName().equals(getUuid()) &&
                    connection.getFromDevice().getName().equals(message.getPreviousHop())) {
                connection.queueMessage(message);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove() {
        /*
        for (Object edge : new ArrayList<>(device.getNetworkGraph().getInEdges(device))) {
            device.getNetworkGraph().removeEdge(edge);
        }
        VertexProperties device.getNetworkGraph().getVerticesProperties().get(device)
        */
        // TODO: Handle broken connections and inform simulation
        return false;
    }
}
