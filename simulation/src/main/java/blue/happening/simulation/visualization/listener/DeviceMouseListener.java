package blue.happening.simulation.visualization.listener;

import java.awt.event.MouseEvent;

import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.entities.Device;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;


public class DeviceMouseListener implements GraphMouseListener<Device> {

    @Override
    public void graphClicked(Device device, MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1) {
            // Remove previous selected device
            boolean wasClicked = device.isClicked();
            for (Device graphDevice : HappeningDemo.getGraph().getVertices()) {
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