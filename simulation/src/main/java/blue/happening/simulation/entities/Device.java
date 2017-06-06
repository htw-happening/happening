package blue.happening.simulation.entities;

import java.util.Observable;

import blue.happening.mesh.MeshHandler;
import blue.happening.mesh.Message;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.visualization.listener.DeviceObserver;


public class Device extends Observable {

    private String name;
    private MeshHandler meshHandler;
    private boolean isClicked = false;
    private boolean isNeighbour = false;
    private boolean isSending;
    private boolean isReceiving;
    private MockLayer mockLayer;
    private NetworkGraph networkGraph;

    public Device(String name, NetworkGraph networkGraph) {
        addObserver(new DeviceObserver(networkGraph));
        this.name = name;
        this.networkGraph = networkGraph;
        mockLayer = new MockLayer();
        meshHandler = new MeshHandler(this.name);
        meshHandler.registerLayer(mockLayer);
        meshHandler.registerCallback(new MockMeshHandlerCallback(this));
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
        if(isClicked){
            notifyDeviceObserver(DeviceObserver.Events.DEVICE_CLICKED);
        } else {
            notifyDeviceObserver(DeviceObserver.Events.DEVICE_UNCLICKED);
        }
    }

    public boolean isNeighbour() {
        return isNeighbour;
    }

    public void setNeighbour(boolean neighbour) {
        isNeighbour = neighbour;
        notifyDeviceObserver(DeviceObserver.Events.BECAME_NEIGHBOUR);
    }

    public String getName() {
        return this.name;
    }

    public NetworkGraph getNetworkGraph() {
        return networkGraph;
    }

    public void sendMessageTo(Device toDevice, Message message) {
        notifyDeviceObserver(DeviceObserver.Events.SEND_MESSAGE);
        toDevice.notifyDeviceObserver(DeviceObserver.Events.RECEIVE_MESSAGE);
        mockLayer.sendMessage(message);
    }

    public void receiveMessage(Message message) {
        mockLayer.getLayerCallback().onMessageReceived(message.toBytes());
    }

    public void connectTo(Device device) {
        mockLayer.addDevice(device);
    }

    public void disconnectFrom(Device device) {
        mockLayer.removeDevice(device);
    }

    public MeshHandler getMeshHandler() {
        return meshHandler;
    }

    public MockLayer getMockLayer() {
        return mockLayer;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device))
            return false;
        else if (o == this)
            return true;
        return this.name.equals(((Device) o).name);
    }

    public void notifyDeviceObserver(DeviceObserver.Events arg) {
        setChanged();
        notifyObservers(arg);
    }
}
