package blue.happening.simulation.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.MeshHandler;
import blue.happening.mesh.Message;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.visualization.listener.DeviceObserver;


public class Device extends Observable {

    private String name;
    private MeshHandler meshHandler;
    private int messageDelay;
    private double txRadius;
    private double rxRadius;
    private boolean isEnabled = true;
    private boolean isClicked = false;
    private boolean isNeighbour = false;
    private MockLayer mockLayer;
    private NetworkGraph networkGraph;
    private List<Message> outBox = new ArrayList<>();

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
        boolean wasClicked = isClicked;
        isClicked = clicked;
        if (!wasClicked && isClicked) {
            notifyDeviceObserver(DeviceObserver.Events.DEVICE_CLICKED, null);
        } else if (wasClicked && !isClicked) {
            notifyDeviceObserver(DeviceObserver.Events.DEVICE_UNCLICKED, null);
        }
    }

    public boolean isNeighbour() {
        return isNeighbour;
    }

    public void setNeighbour(boolean neighbour) {
        boolean wasNeighbour = isNeighbour;
        isNeighbour = neighbour;
        if (!wasNeighbour && isNeighbour) {
            notifyDeviceObserver(DeviceObserver.Events.BECAME_NEIGHBOUR, null);
        } else if (wasNeighbour && !isNeighbour) {
            notifyDeviceObserver(DeviceObserver.Events.IS_NOT_NEIGHBOUR_ANYMORE, null);
        }
    }

    public int getMessageDelay() {
        return messageDelay;
    }

    public void setMessageDelay(int messageDelay) {
        this.messageDelay = messageDelay;
    }

    public double getTxRadius() {
        return isEnabled ? txRadius : 0;
    }

    public void setTxRadius(double txRadius) {
        this.txRadius = txRadius;
    }

    public double getRxRadius() {
        return isEnabled ? rxRadius : 0;
    }

    public void setRxRadius(double rxRadius) {
        this.rxRadius = rxRadius;
    }

    public void toggleEnabled() {
        isEnabled = !isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return this.name;
    }

    public NetworkGraph getNetworkGraph() {
        return networkGraph;
    }

    public List<MeshDevice> getDevices() {
        return getMeshHandler().getDevices();
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
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device))
            return false;
        else if (o == this)
            return true;
        return this.name.equals(((Device) o).name);
    }

    public void notifyDeviceObserver(DeviceObserver.Events arg, Object options) {
        setChanged();
        notifyObservers(new DeviceChangedEvent(arg, options));
    }

    public void addToOutbox(Message message) {
        outBox.add(message);
    }

    public void removeFromOutBox(Message message) {
        outBox.remove(message);
    }

    public boolean isSending() {
        return outBox.size() > 0;
    }

    public boolean isSendingMsgFromOriginator(String uuid) {
        boolean isFromOriginator = false;
        for (Message message : outBox) {
            if (message.getSource().equals(uuid)) {
                isFromOriginator = true;
                break;
            }
        }
        return isFromOriginator;
    }

    public class DeviceChangedEvent {
        private DeviceObserver.Events type;
        private Object options;
        public DeviceChangedEvent(DeviceObserver.Events type, Object options) {
            this.type = type;
            this.options = options;
        }
        public DeviceObserver.Events getType() {
            return type;
        }

        public Object getOptions() {
            return options;
        }
    }
}
