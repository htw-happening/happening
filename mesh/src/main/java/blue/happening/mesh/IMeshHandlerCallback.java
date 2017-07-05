package blue.happening.mesh;


import blue.happening.mesh.statistics.Stat;
import blue.happening.mesh.statistics.StatsResult;

public interface IMeshHandlerCallback {

    void onDeviceAdded(MeshDevice meshDevice);

    void onDeviceUpdated(MeshDevice meshDevice);

    void onDeviceRemoved(MeshDevice meshDevice);

    void onMessageReceived(byte[] message, MeshDevice source);

    void onNetworkStatsUpdated(StatsResult networkStats);

    void logMessage(Message msg, int action);
}
