package blue.happening.mesh;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class RoutingTable extends HashMap<String, RemoteDevice> {

    private IMeshHandlerCallback meshHandlerCallback;

    void registerMeshHandlerCallback(IMeshHandlerCallback meshHandlerCallback){
        this.meshHandlerCallback = meshHandlerCallback;
    }

    List<RemoteDevice> getNeighbours() {
        return values().stream()
                .filter(RemoteDevice::isNeighbour)
                .collect(Collectors.toList());
    }

    /**
     * @param remoteDevice
     * @return returns the neighbour with the best transmission quality or null
     * when no neighbour exist
     */
    RemoteDevice getBestNeighbourForRemoteDevice(RemoteDevice remoteDevice) {
        return values().stream()
                .filter(RemoteDevice::isNeighbour)
                .filter(
                        device -> remoteDevice.getNeighbourUuids().contains(device.getUuid())
                )
                .sorted()
                .findFirst()
                .orElse(null);
    }

    List<RemoteDevice> getExpiredRemoteDevices() {
        return this.values().stream()
                .filter(RemoteDevice::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Overloaded method for {@link RoutingTable#ensureConnection(RemoteDevice, RemoteDevice)}
     * ensureConnection} which retrieves or creates devices from UUIDs.
     *
     * @param remoteDeviceUuid UUID of newly discovered device
     * @param neighbourUuid    UUID of direct neighbour via which remoteDevice is reachable
     */
    void ensureConnection(String remoteDeviceUuid, String neighbourUuid) {
        RemoteDevice remoteDevice = get(remoteDeviceUuid);
        if (remoteDevice == null) {
            remoteDevice = new RemoteDevice(remoteDeviceUuid) {
                public boolean sendMessage(Message message) {
                    throw new NotImplementedException();
                }
            };
        }
        RemoteDevice neighbour = get(neighbourUuid);
        if (neighbour != null) {
            ensureConnection(remoteDevice, neighbour);
        }
    }

    /**
     * Ensure the routing table reflects a path from discoveredDevice via neighbour
     * to us. Makes sure to prefer {@link RemoteDevice} instances that are neighbours
     * and directly reachable via a connected layer.
     *
     * @param discoveredDevice Newly discovered device
     * @param neighbour        Direct neighbour via which remoteDevice is reachable
     * @return If a device already existed with the same Uuid
     * it is returned, if a new entry in the routing table is created,
     * we return null. Behaves like {@link HashMap#put put}
     */
    RemoteDevice ensureConnection(RemoteDevice discoveredDevice,
                                  RemoteDevice neighbour) {
        RemoteDevice existingDevice = get(discoveredDevice.getUuid());
        if (existingDevice == null) {
            // Device did not previously exist
            put(discoveredDevice.getUuid(), discoveredDevice);
            existingDevice = discoveredDevice;
            meshHandlerCallback.onDeviceAdded(discoveredDevice.getUuid());
        } else if (discoveredDevice.isNeighbour()) {
            // Device was a multi hop device and becomes a neighbour
            discoveredDevice.mergeNeighbours(existingDevice);
            put(discoveredDevice.getUuid(), discoveredDevice);
            existingDevice = discoveredDevice;
        } else if (existingDevice.isNeighbour()) {
            // Device was neighbour and also becomes reachable as multi hop
            existingDevice.mergeNeighbours(discoveredDevice);
        } else {
            // Device remains multi hop
        }

        existingDevice.setLastSeen(System.currentTimeMillis());
        existingDevice.getNeighbourUuids().add(neighbour.getUuid());
        return existingDevice;
    }

    @Override
    public RemoteDevice put(String key, RemoteDevice value) {
        RemoteDevice existing = super.put(key, value);
        if (existing == null) {
            // TODO: callback foo
        }
        return existing;
    }

    @Override
    public RemoteDevice remove(Object key) {
        // TODO: Cascade delete into bestNextHop table
        return super.remove(key);
    }

    interface RoutingTableCallback {
        void onDeviceAdded(RemoteDevice remoteDevice);

        void onDeviceRemoved(RemoteDevice remoteDevice);
    }

}
