package blue.happening.simulation.visualization.listener;

import java.util.Collection;
import java.util.List;
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

    public DeviceObserver(NetworkGraph<Device, Connection> graph) {
        this.graph = graph;
    }

    public void update(Observable obj, Object arg) {
        Device device = (Device) obj;
        Device.DeviceChangedEvent event = (Device.DeviceChangedEvent) arg;

        if (event.getType() == Events.DEVICE_CLICKED) {
            this.graph.setClickedDevice(device);
            setDevicePanel(device);

            // Unset all devices that where neighbours of previous device selection
            for (Object object : device.getNetworkGraph().getVertices()) {
                Device graphDevice = (Device) object;
                graphDevice.setNeighbour(false);
            }

            // Set devices that are neighbour of selected device
            for(MeshDevice neighbour: device.getDevices()){
                Device foundDevice = findGraphDevice(neighbour);
                if(foundDevice != null){
                    foundDevice.setNeighbour(true);
                }
            }
        } else if (device.isClicked()) {
            updateDevicePanel(device, event);

            if(event.getType() == Events.NEIGHBOUR_ADDED){
                Device foundDevice = findGraphDevice((MeshDevice) event.getOptions());
                if(foundDevice != null){
                    foundDevice.setNeighbour(true);
                }
            } else if(event.getType() == Events.NEIGHBOUR_REMOVED){
                Device foundDevice = findGraphDevice((MeshDevice) event.getOptions());
                if(foundDevice != null){
                    foundDevice.setNeighbour(false);
                }
            }
        }
    }

    private Device findGraphDevice(MeshDevice meshDevice){
        Device foundGraphDevice = null;
        for (Object object : this.graph.getVertices()){
            Device graphDevice = (Device) object;
            if(graphDevice.getName().equals(meshDevice.getUuid())){
                foundGraphDevice = graphDevice;
                break;
            }
        }
        return foundGraphDevice;
    }

    private void setDevicePanel(Device device) {
        panel = device.getNetworkGraph().getDevicePanel();
        if (panel != null) {
            panel.setDevice(device);
        }
    }

    private void updateDevicePanel(Device device, Device.DeviceChangedEvent event) {
        if (panel != null) {
            panel.updateDevice(device, event);
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
