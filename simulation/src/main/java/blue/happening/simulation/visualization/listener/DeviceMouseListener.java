package blue.happening.simulation.visualization.listener;

import java.awt.event.MouseEvent;

import blue.happening.simulation.entities.Device;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;


public class DeviceMouseListener<V extends Device> implements GraphMouseListener<V> {

    @Override
    public void graphClicked(Device device, MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
            for (Object object : device.getNetworkGraph().getVertices()) {
                Device graphDevice = (Device) object;
                graphDevice.setClicked(false);
                graphDevice.setNeighbour(false);
                if (device.getMeshHandler().getRoutingTable().containsKey(graphDevice.getName())) {
                    graphDevice.setNeighbour(true);
                }
                device.setClicked(true);
            }
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