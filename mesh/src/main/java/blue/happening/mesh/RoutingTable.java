package blue.happening.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class RoutingTable extends ConcurrentHashMap<String, RemoteDevice> {

    private IMeshHandlerCallback meshHandlerCallback;
    private Set<Route> routes;

    public RoutingTable() {
        this.routes = Collections.newSetFromMap(new ConcurrentHashMap<Route, Boolean>());
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

    List<Route> getBestRoutesTo(RemoteDevice remoteDevice) {
        List<Route> bestRoutes = new ArrayList<>();
        for (Route route : routes) {
            RemoteDevice toDevice = get(route.getToDevice());
            if (toDevice.equals(remoteDevice)) {
                bestRoutes.add(route);
            }
        }
        Collections.sort(bestRoutes, Collections.<Route>reverseOrder());
        return bestRoutes;
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
     * Overloaded method for {@link RoutingTable#putRoute(RemoteDevice, RemoteDevice)}
     * putRoute} which retrieves or creates devices from UUIDs.
     *
     * @param remoteDeviceUuid UUID of newly discovered device
     * @param viaDeviceUuid    UUID of direct neighbour via which remoteDevice is reachable
     */
    void putRoute(String remoteDeviceUuid, String viaDeviceUuid) {
        RemoteDevice remoteDevice = get(remoteDeviceUuid);
        if (remoteDevice == null) {
            remoteDevice = new RemoteDevice(remoteDeviceUuid) {
                @Override
                public boolean sendMessage(Message message) {
                    throw new UnsupportedOperationException("Device " + getUuid() + " cannot send");
                }

                @Override
                public boolean remove() {
                    return false;
                }
            };
        }
        putRoute(remoteDevice, get(viaDeviceUuid));
    }

    /**
     * Ensure the routing table reflects a path from discoveredDevice via neighbour
     * to us. Makes sure to prefer {@link RemoteDevice} instances that are neighbours
     * and directly reachable via a connected layer.
     *
     * @param discoveredDevice Newly discovered device
     * @param viaDevice        Direct neighbour via which remoteDevice is reachable
     */
    void putRoute(RemoteDevice discoveredDevice, RemoteDevice viaDevice) {
        RemoteDevice existingDevice = get(discoveredDevice.getUuid());

        if (existingDevice == null) {
            // discovered new device
            put(discoveredDevice.getUuid(), discoveredDevice);
        } else if (viaDevice.equals(discoveredDevice) && !isNeighbour(existingDevice)) {
            // multi hop now also neighbour
            put(discoveredDevice.getUuid(), discoveredDevice);
        } else {
            // routes to device do not change
            discoveredDevice = existingDevice;
        }

        discoveredDevice.setLastSeen(System.currentTimeMillis());
        routes.add(new Route(viaDevice.getUuid(), discoveredDevice.getUuid()));
    }

    @Override
    public RemoteDevice put(String uuid, RemoteDevice remoteDevice) {
        RemoteDevice existing = super.put(uuid, remoteDevice);
        if (isReachable(existing)) {
            meshHandlerCallback.onDeviceUpdated(remoteDevice.getMeshDevice());
        } else {
            meshHandlerCallback.onDeviceAdded(remoteDevice.getMeshDevice());
        }
        return existing;
    }

    boolean flushExpiredRemoteDevices() {
        for (RemoteDevice device : values()) {
            if (device.isExpired()) {
                remove(device);
            }
        }
        return flush();
    }

    void removeRoutesVia(RemoteDevice remoteDevice) {
        Iterator<Route> i = routes.iterator();
        while (i.hasNext()) {
            Route route = i.next();
            if (route.getViaDevice().equals(remoteDevice.getUuid())) {
                // Remove any route via remoteDevice
                i.remove();
                remoteDevice.getEchoSlidingWindow().clear();
                RemoteDevice toDevice = get(route.getToDevice());
                if (!isReachable(toDevice)) {
                    // Also remove any device that has no routes any more
                    // after routeDevice is gone
                    remove(route.getToDevice());
                }
            }
        }
    }

    private void removeRoutesTo(RemoteDevice remoteDevice) {
        Iterator<Route> i = routes.iterator();
        while (i.hasNext()) {
            Route route = i.next();
            if (route.getToDevice().equals(remoteDevice.getUuid())) {
                // Remove any route to remoteDevice
                i.remove();
            }
        }
    }

    private boolean isReachable(RemoteDevice remoteDevice) {
        if (remoteDevice != null) {
            for (Route route : routes) {
                RemoteDevice toDevice = get(route.getToDevice());
                if (toDevice.equals(remoteDevice)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNeighbour(RemoteDevice remoteDevice) {
        for (Route route : routes) {
            RemoteDevice viaDevice = get(route.getViaDevice());
            RemoteDevice toDevice = get(route.getToDevice());
            if (viaDevice != null && toDevice != null) {
                if (viaDevice.equals(remoteDevice) &&
                        toDevice.equals(remoteDevice)) {
                    return true;
                }
            }
        }
        return false;
    }

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


    /**
     * Remove all devices that are not reachable
     *
     * @return Whether at least one device has been flushed
     */
    boolean flush() {
        boolean flushed = false;
        for (String key : Collections.list(keys())) {
            RemoteDevice remoteDevice = get(key);
            if (!isReachable(remoteDevice)) {
                RemoteDevice removedDevice = super.remove(key);
                flushed |= remoteDevice == null;
            }
        }
        return flushed;
    }
}
