package blue.happening.simulation.visualization.listener;

import java.util.Observable;
import java.util.Observer;

import blue.happening.mesh.MeshDevice;
import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.visualization.DevicePanel;


public class DeviceObserver implements Observer {

    @Override
    public void update(Observable obj, Object arg) {
        Device device = (Device) obj;
        Device.DeviceChangedEvent event = (Device.DeviceChangedEvent) arg;

        if (event.getType() == Events.DEVICE_CLICKED) {
            HappeningDemo.getGraph().setClickedDevice(device);
            setDevicePanel(device);

            // Unset all devices that where neighbours of previous device selection
            for (Device vertex : HappeningDemo.getGraph().getVertices()) {
                vertex.setNeighbour(false);
            }

            // Set devices that are neighbour of selected device
            for (MeshDevice neighbour : device.getDevices()) {
                Device foundDevice = findGraphDevice(neighbour);
                if (foundDevice != null) {
                    foundDevice.setNeighbour(true);
                }
            }
        } else if (device.isClicked()) {
            updateDevicePanel(device, event);

            if (event.getType() == Events.NEIGHBOUR_ADDED) {
                Device foundDevice = findGraphDevice((MeshDevice) event.getOptions());
                if (foundDevice != null) {
                    foundDevice.setNeighbour(true);
                }
            } else if (event.getType() == Events.NEIGHBOUR_REMOVED) {
                Device foundDevice = findGraphDevice((MeshDevice) event.getOptions());
                if (foundDevice != null) {
                    foundDevice.setNeighbour(false);
                }
            }
        }
    }

    private Device findGraphDevice(MeshDevice meshDevice) {
        Device foundGraphDevice = null;
        for (Device device : HappeningDemo.getGraph().getVertices()) {
            if (device.getName().equals(meshDevice.getUuid())) {
                foundGraphDevice = device;
                break;
            }
        }
        return foundGraphDevice;
    }

    private void setDevicePanel(Device device) {
        DevicePanel panel = HappeningDemo.getFrame().getDevicePanel();
        if (panel != null) {
            panel.setDevice(device);
        }
    }

    private void updateDevicePanel(Device device, Device.DeviceChangedEvent event) {
        DevicePanel panel = HappeningDemo.getFrame().getDevicePanel();
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
        NETWORK_STATS_UPDATED,
        OGM_LOG_ITEM_ADDED,
        UCM_LOG_ITEM_ADDED
    }
}
