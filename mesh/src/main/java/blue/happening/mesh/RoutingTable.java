package blue.happening.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RoutingTable extends ConcurrentHashMap<String, RemoteDevice> {

    private IMeshHandlerCallback meshHandlerCallback;

    void registerMeshHandlerCallback(IMeshHandlerCallback meshHandlerCallback) {
        this.meshHandlerCallback = meshHandlerCallback;
    }

    List<RemoteDevice> getNeighbours() {
        List<RemoteDevice> neighbourList = new ArrayList<>();
        for (RemoteDevice device : values()) {
            if (device.isNeighbour()) {
                neighbourList.add(device);
            }
        }
        return neighbourList;
    }

    /**
     * @param remoteDevice
     * @return returns the neighbour with the best transmission quality or null
     * when no neighbour exist
     */
    RemoteDevice getBestNeighbourForRemoteDevice(RemoteDevice remoteDevice) {
        List<RemoteDevice> bestNeighboursForRemoteDevice = new ArrayList<>();

        for (RemoteDevice neighbour : getNeighbours()) {
            if (remoteDevice.getNeighbourUuids().contains(neighbour.getUuid())) {
                bestNeighboursForRemoteDevice.add(neighbour);
            }
        }
        Collections.sort(bestNeighboursForRemoteDevice, Collections.<RemoteDevice>reverseOrder());
        return bestNeighboursForRemoteDevice.get(0);
    }

    List<RemoteDevice> getExpiredRemoteDevices() {
        List<RemoteDevice> expiredRemoteDevices = new ArrayList<>();
        for (RemoteDevice device : values()) {
            if (device.isExpired()) {
                expiredRemoteDevices.add(device);
            }
        }
        return expiredRemoteDevices;
    }

    List<MeshDevice> getReachableMeshDevices() {
        List<MeshDevice> meshDevices = new ArrayList<>();
        for (Map.Entry<String, RemoteDevice> entry : entrySet()) {
            RemoteDevice remoteDevice = entry.getValue();
            if (remoteDevice.isReachable()) {
                meshDevices.add(remoteDevice.getMeshDevice());
            }
        }
        return meshDevices;
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
                @Override
                public boolean sendMessage(Message message) {
                    System.out.println("DEVICE " + this.getUuid() + " DOES NOT HAVE THIS OP");
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean remove() {
                    return false;
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
        } else {
            // When discovered and neighbour are the same add neighbour to make sure that
            // discovered device is handled as neighbour
            if (discoveredDevice.equals(neighbour)) {
                discoveredDevice.getNeighbourUuids().add(neighbour.getUuid());
            }
            if (discoveredDevice.isNeighbour() && !existingDevice.isNeighbour()) {
                // Device was a multi hop device and becomes a neighbour
                discoveredDevice.mergeNeighbours(existingDevice);
                put(discoveredDevice.getUuid(), discoveredDevice);
                existingDevice = discoveredDevice;
            } else if (!discoveredDevice.isNeighbour() && existingDevice.isNeighbour()) {
                // Device was neighbour and also becomes reachable as multi hop
                existingDevice.mergeNeighbours(discoveredDevice);
            } else {
                // Device remains multi hop
            }
        }

        existingDevice.setLastSeen(System.currentTimeMillis());
        existingDevice.getNeighbourUuids().add(neighbour.getUuid());
        return existingDevice;
    }

    @Override
    public RemoteDevice put(String key, RemoteDevice value) {
        RemoteDevice existing = super.put(key, value);
        if (existing == null || !existing.isReachable()) {
            meshHandlerCallback.onDeviceAdded(value.getMeshDevice());
        } else {
            meshHandlerCallback.onDeviceUpdated(value.getMeshDevice());
        }
        return existing;
    }

    void removeAsNeighbour(String uuid) {
        // A neighbour device is not directly connected anymore, so we remove it from other devices
        // neighbour lists, clear its echo window and trigger a remove callback if it is no longer
        // reachable via another neighbour.
        for (RemoteDevice device : values()) {
            device.getNeighbourUuids().remove(uuid);
            if (device.getUuid().equals(uuid)) {
                device.getEchoSlidingWindow().clear();
            }
            if (!device.isReachable()) {
                device.getReceiveSlidingWindow().clear();
                meshHandlerCallback.onDeviceRemoved(device.getMeshDevice());
            }
        }
    }

    public RemoteDevice remove(Object key) {
        RemoteDevice existing = get(key);
        if (existing != null) {
            removeAsNeighbour(existing.getUuid());
        }
        RemoteDevice deleted = super.remove(key);
        if (deleted != null) {
            meshHandlerCallback.onDeviceRemoved(deleted.getMeshDevice());
        }
        return deleted;
    }
}
