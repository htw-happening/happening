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
        meshHandler.registerCallback(new MockMeshHandlerCallback());
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isNeighbour() {
        return isNeighbour;
    }

    public void setNeighbour(boolean neighbour) {
        isNeighbour = neighbour;
    }

    public String getName() {
        return this.name;
    }

    public NetworkGraph getNetworkGraph() {
        return networkGraph;
    }

    public void sendMessageTo(Device toDevice, Message message) {
        notifyDeviceObserver("sending");
        toDevice.notifyDeviceObserver("receiving");
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

    public void setIsSending(boolean isSending) {
        this.isSending = isSending;
    }

    public void setIsReceiving(boolean isReceiving) {
        this.isReceiving = isReceiving;
    }

    public boolean isSending() {
        return isSending;
    }

    public boolean isReceiving() {
        return isReceiving;
    }

    public void notifyDeviceObserver(String arg) {
        setChanged();
        notifyObservers(arg);
    }
}
