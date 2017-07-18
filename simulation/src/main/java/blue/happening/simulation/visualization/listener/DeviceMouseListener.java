package blue.happening.simulation.visualization.listener;

import java.awt.event.MouseEvent;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;


public class DeviceMouseListener<V extends Device> implements GraphMouseListener<V> {

    private NetworkGraph<Device, Connection> networkGraph;

    @Override
    public void graphClicked(Device device, MouseEvent me) {
        if (networkGraph == null) {
            networkGraph = device.getNetworkGraph();
        }
        if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
            // Remove previous selected device
            boolean wasClicked = device.isClicked();
            for (Device graphDevice : networkGraph.getVertices()) {
                graphDevice.setClicked(false);
                graphDevice.setNeighbour(false);
            }
            device.setClicked(!wasClicked);
        }
        me.consume();
    }

    @Override
    public void graphPressed(Device device, MouseEvent me) {
    }

    @Override
    public void graphReleased(Device device, MouseEvent me) {
    }
}