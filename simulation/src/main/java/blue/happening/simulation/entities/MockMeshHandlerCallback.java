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

    private UUID messageId;

    @Override
    public void onMessageLogged(Message message, int status) {
        if (status == MeshHandler.MESSAGE_ACTION_ARRIVED ||
                status == MeshHandler.MESSAGE_ACTION_FORWARDED ||
                status == MeshHandler.MESSAGE_ACTION_SENT) {
            messageId = UUID.randomUUID();
        }
        LogItem logItem = new LogItem(message, status, messageId);
        switch (message.getType()) {
            case MeshHandler.MESSAGE_TYPE_OGM:
                device.getOgmLog().push(logItem);
                device.notifyDeviceObserver(DeviceObserver.Events.OGM_LOG_ITEM_ADDED, logItem);
                break;
            case MeshHandler.MESSAGE_TYPE_UCM:
                device.getUcmLog().push(logItem);
                device.notifyDeviceObserver(DeviceObserver.Events.UCM_LOG_ITEM_ADDED, logItem);
                break;
        }
    }
}
