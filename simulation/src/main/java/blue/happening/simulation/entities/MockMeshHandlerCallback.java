package blue.happening.simulation.entities;

import java.util.UUID;

import blue.happening.mesh.IMeshHandlerCallback;
import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.MeshHandler;
import blue.happening.mesh.Message;
import blue.happening.mesh.statistics.StatsResult;
import blue.happening.simulation.visualization.listener.DeviceObserver;


class MockMeshHandlerCallback implements IMeshHandlerCallback {

    private Device device;

    MockMeshHandlerCallback(Device device) {
        this.device = device;
    }

    @Override
    public void onMessageReceived(byte[] message, MeshDevice meshDevice) {
        System.out.println(meshDevice.getUuid() + ": " + new String(message));
    }

    @Override
    public void onDeviceAdded(MeshDevice meshDevice) {
        device.notifyDeviceObserver(DeviceObserver.Events.NEIGHBOUR_ADDED, meshDevice);
    }

    @Override
    public void onDeviceUpdated(MeshDevice meshDevice) {
        device.notifyDeviceObserver(DeviceObserver.Events.NEIGHBOUR_UPDATED, meshDevice);
    }

    @Override
    public void onDeviceRemoved(MeshDevice meshDevice) {
        device.notifyDeviceObserver(DeviceObserver.Events.NEIGHBOUR_REMOVED, meshDevice);
    }

    @Override
    public void onNetworkStatsUpdated(StatsResult networkStats) {
        device.notifyDeviceObserver(DeviceObserver.Events.NETWORK_STATS_UPDATED, networkStats);
    }

    @Override
    public void onMessageLogged(Message message, int status) {
        //TODO check why UCM messages are added twice into UCM log
        if (status == MeshHandler.MESSAGE_ACTION_RECEIVED && message.getType() == MeshHandler.MESSAGE_TYPE_UCM) {
            System.out.println(device.getName() + " received " + new String(message.getBody()));
        }

        LogItem logItem;
        LogQueue logQueue;
        if (message.getType() == MeshHandler.MESSAGE_TYPE_OGM) {
            logQueue = device.getOgmLog();
        } else {
            logQueue = device.getUcmLog();
        }

        if (logQueue.containsKey(message.getUuid())) {
            logItem = logQueue.get(message.getUuid());
            logItem.setStatus(status);
        } else {
            logItem = new LogItem(message, status, message.getUuid());
        }

        switch (message.getType()) {
            case MeshHandler.MESSAGE_TYPE_OGM:
                device.getOgmLog().put(logItem);
                device.notifyDeviceObserver(DeviceObserver.Events.OGM_LOG_ITEM_ADDED, logItem);
                break;
            case MeshHandler.MESSAGE_TYPE_UCM:
                device.getUcmLog().put(logItem);
                device.notifyDeviceObserver(DeviceObserver.Events.UCM_LOG_ITEM_ADDED, logItem);
                break;
        }
    }
}
