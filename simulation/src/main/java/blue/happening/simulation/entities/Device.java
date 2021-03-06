package blue.happening.simulation.entities;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.ScheduledExecutorService;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.MeshHandler;
import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.graph.MeshGraph;
import blue.happening.simulation.graph.internal.VertexProperties;
import blue.happening.simulation.visualization.listener.DeviceObserver;


public class Device extends Observable {

    private String name;
    private MeshHandler meshHandler;
    private int messageDelay;
    private boolean isEnabled = true;
    private boolean isNeighbour = false;
    private MockLayer mockLayer;
    private ScheduledExecutorService runner;
    private LogQueue ucmLog;
    private LogQueue ogmLog;

    public Device(String name, ScheduledExecutorService runner, int messageDelay, float messageLoss) {
        addObserver(new DeviceObserver());
        this.name = name;
        this.runner = runner;
        this.messageDelay = messageDelay;
        mockLayer = new MockLayer();
        mockLayer.setMessageLoss(messageLoss);
        meshHandler = new MeshHandler(this.name, runner);
        meshHandler.registerLayer(mockLayer);
        meshHandler.registerCallback(new MockMeshHandlerCallback(this));
        ucmLog = new LogQueue(16);
        ogmLog = new LogQueue(64);
    }

    public boolean isClicked() {
        MeshGraph graph = HappeningDemo.getGraph();
        Device clicked = graph == null ? null : graph.getClickedDevice();
        return clicked != null && clicked.equals(this);
    }

    public void setClicked(boolean clicked) {
        boolean wasClicked = isClicked();
        if (!wasClicked && clicked) {
            HappeningDemo.getGraph().setClickedDevice(this);
            notifyDeviceObserver(DeviceObserver.Events.DEVICE_CLICKED, null);
        } else if (wasClicked && !clicked) {
            HappeningDemo.getGraph().setClickedDevice(null);
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

    public void setMessageLoss(float messageLoss) {
        mockLayer.setMessageLoss(messageLoss);
    }

    public void toggleEnabled() {
        MeshGraph graph = HappeningDemo.getGraph();
        VertexProperties<Device, Connection> properties = graph.getVertexProperties(this);
        if (isEnabled) {
            graph.removeEdges(this);
            properties.getRxRadius().setValue(0);
            properties.getTxRadius().setValue(0);
        } else {
            graph.addEdges(this);
            properties.getRxRadius().setValue(properties.getRxRadius().getInitialValue());
            properties.getTxRadius().setValue(properties.getTxRadius().getInitialValue());
        }
        isEnabled = !isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return this.name;
    }

    public ScheduledExecutorService getRunner() {
        return runner;
    }

    public List<MeshDevice> getDevices() {
        return getMeshHandler().getDevices();
    }

    public LogQueue getUcmLog() {
        return ucmLog;
    }

    public LogQueue getOgmLog() {
        return ogmLog;
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

    void notifyDeviceObserver(DeviceObserver.Events arg, Object options) {
        setChanged();
        notifyObservers(new DeviceChangedEvent(arg, options));
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

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public class DeviceChangedEvent {
        private DeviceObserver.Events type;
        private Object options;

        DeviceChangedEvent(DeviceObserver.Events type, Object options) {
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
