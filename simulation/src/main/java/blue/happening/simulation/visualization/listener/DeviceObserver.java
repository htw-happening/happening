package blue.happening.simulation.visualization.listener;

import java.util.Observable;
import java.util.Observer;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;


public class DeviceObserver implements Observer {
    public enum Events {
        NEIGHBOUR_ADDED,
        NEIGHBOUR_REMOVED,
        BECAME_NEIGHBOUR,
        DEVICE_CLICKED,
        DEVICE_UNCLICKED,
        SEND_MESSAGE,
        RECEIVE_MESSAGE
    }

    private NetworkGraph<Device, Connection> graph;

    public DeviceObserver(NetworkGraph graph) {
        this.graph = graph;
    }

    public void update(Observable obj, Object arg) {
        Device device = (Device) obj;

        if (device.isClicked()) {
            for (Object object : device.getNetworkGraph().getVertices()) {
                Device graphDevice = (Device) object;
                graphDevice.setNeighbour(false);
                if (device.getMeshHandler().getRoutingTable().containsKey(graphDevice.getName())) {
                    graphDevice.setNeighbour(true);
                }
            }
        }
    }
}
