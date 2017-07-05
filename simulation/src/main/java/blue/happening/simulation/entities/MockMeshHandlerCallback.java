package blue.happening.simulation.entities;

import blue.happening.mesh.IMeshHandlerCallback;
import blue.happening.mesh.MeshDevice;
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
}
