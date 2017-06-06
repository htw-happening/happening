package blue.happening.simulation.entities;

import blue.happening.mesh.Message;
import blue.happening.mesh.RemoteDevice;


public class MockRemoteDevice extends RemoteDevice {

    private Device device;

    public MockRemoteDevice(String uuid) {
        super(uuid);
    }

    public boolean sendMessage(Message message) {
        device.getMockLayer().sendMessage(message);
    }

    @Override
    public void remove() {
    /*for (Object edge : new ArrayList<>(device.getNetworkGraph().getInEdges(device))) {
      //device.getNetworkGraph().removeEdge(edge);
    }
    VertexProperties device.getNetworkGraph().getVerticesProperties().get(device) */
        // TODO: Handle broken connections and inform bla
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
