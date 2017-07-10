package blue.happening.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RoutingTable extends ConcurrentHashMap<String, RemoteDevice> {

    private IMeshHandlerCallback meshHandlerCallback;
    private HashSet<Route> routes;

    public RoutingTable() {
        this.routes = new HashSet<>();
    }

    void registerMeshHandlerCallback(IMeshHandlerCallback meshHandlerCallback) {
        this.meshHandlerCallback = meshHandlerCallback;
    }

    List<RemoteDevice> getNeighbours() {
        List<RemoteDevice> neighbourList = new ArrayList<>();
        for (RemoteDevice device : values()) {
            if (isNeighbour(device)) {
                neighbourList.add(device);
            }
        }
        return neighbourList;
    }

    List<Route> getBestRoutesToDestination(RemoteDevice destination) {
        List<Route> bestRoutes = new ArrayList<>();
        for (Route route : routes) {
            if (route.getToDevice().equals(destination)) {
                bestRoutes.add(route);
            }
        }
        Collections.sort(bestRoutes, Collections.<Route>reverseOrder());
        return bestRoutes;
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
            if (isReachable(remoteDevice)) {
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
                routes.add(new Route(neighbour, discoveredDevice));
            }
            if (isNeighbour(discoveredDevice) && !isNeighbour(existingDevice)) {
                // Device was a multi hop device and becomes a neighbour
                discoveredDevice.mergeRoutes(existingDevice);
                put(discoveredDevice.getUuid(), discoveredDevice);
                existingDevice = discoveredDevice;
            } else if (!isNeighbour(discoveredDevice) && isNeighbour(existingDevice)) {
                // Device was neighbour and also becomes reachable as multi hop
                existingDevice.mergeRoutes(discoveredDevice);
            } else {
                // Device remains multi hop
            }
        }

        existingDevice.setLastSeen(System.currentTimeMillis());
        routes.add(new Route(neighbour, existingDevice));
        return existingDevice;
    }

    @Override
    public RemoteDevice put(String key, RemoteDevice value) {
        RemoteDevice existing = super.put(key, value);
        if (existing == null || !isReachable(existing)) {
            meshHandlerCallback.onDeviceAdded(value.getMeshDevice());
        } else {
            meshHandlerCallback.onDeviceUpdated(value.getMeshDevice());
        }
        return existing;
    }

    void removeRoutesVia(RemoteDevice remoteDevice) {
        Iterator<Route> i = routes.iterator();
        while (i.hasNext()) {
            Route route = i.next();
            if (route.getViaDevice().equals(remoteDevice)) {
                // Remove any route via remoteDevice
                i.remove();
                remoteDevice.getEchoSlidingWindow().clear();
                if (!isReachable(route.getToDevice())) {
                    // Also remove any device that has no routes any more
                    // after routeDevice is gone
                    remove(route.getToDevice().getUuid());
                }
            }
        }
    }

    private void removeRoutesTo(RemoteDevice remoteDevice) {
        Iterator<Route> i = routes.iterator();
        while (i.hasNext()) {
            Route route = i.next();
            if (route.getToDevice().equals(remoteDevice)) {
                // Remove any route to remoteDevice
                i.remove();
            }
        }
    }

    boolean isReachable(RemoteDevice remoteDevice) {
        for (Route route : routes) {
            if (route.getToDevice().equals(remoteDevice)) {
                return true;
            }
        }
        return false;
    }

    boolean isNeighbour(RemoteDevice remoteDevice) {
        for (Route route : routes) {
            if (route.getViaDevice().equals(remoteDevice) && route.getToDevice().equals(remoteDevice)) {
                return true;
            }
        }
        return false;
    }

    void

    @Override
    public RemoteDevice remove(Object key) {
        RemoteDevice existing = get(key);
        if (existing != null) {
            removeRoutesTo(existing);
            removeRoutesVia(existing);
            existing.getReceiveSlidingWindow().clear();
            meshHandlerCallback.onDeviceRemoved(existing.getMeshDevice());
        }
        return existing;
    }

    void flush() {
        for (String key : Collections.list(keys())) {
            RemoteDevice remoteDevice = get(key);
            if (!isReachable(remoteDevice)) {
                super.remove(key);
            }
        }
    }
}
