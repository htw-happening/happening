package blue.happening.simulation.visualization.listener;

import java.util.Observable;
import java.util.Observer;

import blue.happening.mesh.MeshDevice;
import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.visualization.DevicePanel;


public class DeviceObserver implements Observer {
    private NetworkGraph<Device, Connection> graph;
    private DevicePanel panel;

    public DeviceObserver(NetworkGraph graph) {
        this.graph = graph;
    }

    public void update(Observable obj, Object arg) {
        Device device = (Device) obj;
        Events event = (Events) arg;

        if (device.isClicked() &&
                (event == Events.DEVICE_CLICKED || event == Events.NEIGHBOUR_ADDED || event == Events.NEIGHBOUR_REMOVED)
                ) {
            for (Object object : device.getNetworkGraph().getVertices()) {
                Device graphDevice = (Device) object;
                graphDevice.setNeighbour(false);
                for (MeshDevice neighbour : device.getDevices()) {
                    if (neighbour.getUuid().equals(graphDevice.getName())) {
                        graphDevice.setNeighbour(true);
                        break;
                    }
                }
            }
        }

        if (device.isClicked() && event == Events.NEIGHBOUR_UPDATED) {
            updateDevicePanel(device);
        }

        if (event == Events.DEVICE_CLICKED) {
            this.graph.setClickedDevice(device);
            setDevicePanel(device);
        }
    }

    private void setDevicePanel(Device device) {
        panel = device.getNetworkGraph().getDevicePanel();
        if (panel != null) {
            panel.setDevice(device);
        }
    }

    private void updateDevicePanel(Device device){
        if (panel != null) {
            panel.updateDevice(device);
        }
    }

    public enum Events {
        NEIGHBOUR_ADDED,
        NEIGHBOUR_UPDATED,
        NEIGHBOUR_REMOVED,
        BECAME_NEIGHBOUR,
        IS_NOT_NEIGHBOUR_ANYMORE,
        DEVICE_CLICKED,
        DEVICE_UNCLICKED,
        SEND_MESSAGE,
        RECEIVE_MESSAGE
    }
}
