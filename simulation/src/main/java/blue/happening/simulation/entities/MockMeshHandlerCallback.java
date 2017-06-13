package blue.happening.simulation.entities;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blue.happening.mesh.IMeshHandlerCallback;
import blue.happening.simulation.visualization.listener.DeviceObserver;


public class MockMeshHandlerCallback implements IMeshHandlerCallback {

    private static Logger logger = LogManager.getLogger(MockMeshHandlerCallback.class);
    private Device device;

    public MockMeshHandlerCallback(Device device) {
        this.device = device;
    }

    @Override
    public void onMessageReceived(String message) {
        logger.debug("App: Message Received");
    }

    @Override
    public void onDeviceAdded(String uuid) {
        logger.debug("App: Device added");
        device.notifyDeviceObserver(DeviceObserver.Events.NEIGHBOUR_ADDED);
    }

    @Override
    public void onDeviceRemoved(String uuid) {
        logger.debug("App: Device removed");
        device.notifyDeviceObserver(DeviceObserver.Events.NEIGHBOUR_REMOVED);
    }
}
