package blue.happening.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        Collections.sort(bestNeighboursForRemoteDevice);
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
                    System.out.println("DEVICE " + this.getUuid() + " DOES NOT HAVE THIS OP");
                    throw new UnsupportedOperationException();
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
        } else {
            // When discovered and neighbour are the same add neighbour to make sure that
            // discovered device is handled as neighbour
            if (discoveredDevice.equals(neighbour)) {
                discoveredDevice.getNeighbourUuids().add(neighbour.getUuid());
            }

            if (discoveredDevice.isNeighbour()) {
                // Device was a multi hop device and becomes a neighbour
                discoveredDevice.mergeNeighbours(existingDevice);
                discoveredDevice.getEchoSlidingWindow().clear();
                discoveredDevice.getReceiveSlidingWindow().clear();
                put(discoveredDevice.getUuid(), discoveredDevice);
                existingDevice = discoveredDevice;
            } else if (existingDevice.isNeighbour()) {
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
        //if (existing == null) {
        meshHandlerCallback.onDeviceAdded(value.getUuid());
        //}
        return existing;
    }

    public void removeFromNeighbours(String uuid) {
        // remove device from neighbour list from all devices where it is listed as neighbour
        for (RemoteDevice device : values()) {
            device.getNeighbourUuids().remove(uuid);

            if (device.getNeighbourUuids().size() == 0) {
                super.remove(device.getUuid());
                meshHandlerCallback.onDeviceRemoved(device.getUuid());
            }
        }
    }

    public RemoteDevice remove(Object key) {
        RemoteDevice existing = get(key);
        if (existing != null) {
            removeFromNeighbours(existing.getUuid());
        }
        RemoteDevice deleted = super.remove(key);
        if (deleted != null) {
            meshHandlerCallback.onDeviceRemoved(deleted.getUuid());
        }
        return deleted;
    }

    interface RoutingTableCallback {
        void onDeviceAdded(RemoteDevice remoteDevice);

        void onDeviceRemoved(RemoteDevice remoteDevice);
    }

}
