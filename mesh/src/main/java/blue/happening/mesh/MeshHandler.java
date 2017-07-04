package blue.happening.mesh;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class MeshHandler {

    public static final int HOP_PENALTY = 15;
    public static final int INITIAL_MAX_SEQUENCE = 512;
    public static final int INITIAL_MESSAGE_TQ = 255;
    public static final int INITIAL_MESSAGE_TTL = 5;
    public static final int INITIAL_MIN_SEQUENCE = 0;
    public static final int MESSAGE_TYPE_OGM = 1;
    public static final int MESSAGE_TYPE_UCM = 2;
    public static final int OGM_INTERVAL = 2;
    public static final int PURGE_INTERVAL = 200;
    public static final int SLIDING_WINDOW_SIZE = 12;
    public static final long DEVICE_EXPIRATION_DURATION = 200;
    public static final String BROADCAST_ADDRESS = "BROADCAST";

    private final RoutingTable routingTable;
    private final Router router;
    private final ILayerCallback layerCallback;
    private final String uuid;
    private IMeshHandlerCallback meshHandlerCallback; // TODO: should be list
    private int sequence;

    public MeshHandler(String uuid) {
        this.uuid = uuid;
        sequence = ThreadLocalRandom.current().nextInt(INITIAL_MIN_SEQUENCE, INITIAL_MAX_SEQUENCE);
        routingTable = new RoutingTable();
        router = new Router(routingTable, uuid);
        layerCallback = new LayerCallback();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(
                new OGMRunner(),
                ThreadLocalRandom.current().nextInt(OGM_INTERVAL),
                OGM_INTERVAL, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(
                new PurgeRunner(),
                ThreadLocalRandom.current().nextInt(PURGE_INTERVAL),
                PURGE_INTERVAL, TimeUnit.SECONDS);
    }

    public void registerLayer(Layer layer) {
        layer.registerLayerCallback(layerCallback);
    }

    public void registerCallback(IMeshHandlerCallback callback) {
        meshHandlerCallback = callback;
        routingTable.registerMeshHandlerCallback(callback);
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public List<MeshDevice> getDevices() {
        return routingTable.getReachableMeshDevices();
    }

    public boolean sendMessage(byte[] message, String uuid) {
        System.out.println("MeshHandler sendMessage " + new String(message) + " to " + uuid);
        String s = "";
        for (Map.Entry<String, RemoteDevice> stringRemoteDeviceEntry : routingTable.entrySet()) {
            s += stringRemoteDeviceEntry.getKey() + ", ";
        }
        System.out.println("MeshHandler routingTable values " + s);

        RemoteDevice remoteDevice = routingTable.get(uuid);
        if (remoteDevice == null) {
            System.out.println("MeshHandler found NO device in routingTable for uuid " + uuid);
            return false;
        } else {
            Message ucm = new Message(this.uuid, uuid, INITIAL_MIN_SEQUENCE, MESSAGE_TYPE_UCM, message);
            try {
                ucm = router.routeMessage(ucm);
            } catch (Router.RoutingException e) {
                e.printStackTrace();
            }
            return ucm != null;
        }
    }

    private class OGMRunner implements Runnable {
        @Override
        public void run() {
            try {
                Message message = new Message(uuid, BROADCAST_ADDRESS, sequence, MESSAGE_TYPE_OGM, null);
                for (RemoteDevice remoteDevice : routingTable.getNeighbours()) {
                    System.out.println("OGM SENT: " + message);
                    remoteDevice.sendMessage(message);
                    remoteDevice.getEchoSlidingWindow().slideSequence(sequence);
                }
                sequence++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class PurgeRunner implements Runnable {
        @Override
        public void run() {
            try {
                for (RemoteDevice remoteDevice : routingTable.getExpiredRemoteDevices()) {
                    routingTable.remove(remoteDevice.getUuid());
                    remoteDevice.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class LayerCallback implements ILayerCallback {

        @Override
        public void onDeviceAdded(RemoteDevice remoteDevice) {
            System.out.println("DEVICE ADDED: " + remoteDevice);
            routingTable.ensureConnection(remoteDevice, remoteDevice);
        }

        @Override
        public void onDeviceRemoved(RemoteDevice remoteDevice) {
            System.out.println("DEVICE REMOVED: " + remoteDevice);
            routingTable.removeAsNeighbour(remoteDevice.getUuid());
        }

        @Override
        public void onMessageReceived(byte[] bytes) {
            Message message, propagate;

            try {
                message = Message.fromBytes(bytes);
                if (message == null) {
                    throw new Exception("Could not parse message");
                }
            } catch (Exception e) {
                System.out.println("MESSAGE BROKEN: " + e.getMessage());
                return;
            }

            try {
                propagate = router.routeMessage(message);
            } catch (Router.RoutingException e) {
                System.out.println("ROUTING FAILED: " + e.getMessage());
                return;
            }

            if (propagate != null) {
                MeshDevice source = routingTable.get(message.getSource()).getMeshDevice();
                meshHandlerCallback.onMessageReceived(message.getBody(), source);
            }

            // Check whether message is an echo OGM
            if (!message.getSource().equals(uuid)) {
                RemoteDevice source = routingTable.get(message.getSource());
                if (source == null) {
                    System.out.println("MeshHandler/onMessageReceived: Source not in routing table " + uuid + ": " + message.getSource());
                    return;
                } else {
                    System.out.println("MeshHandler/onMessageReceived: Source is in routing table " + message.getSource());
                }
                // TODO: Move this block to a better location
                MeshDevice meshDevice = source.getMeshDevice();
                meshDevice.setReceivedSize(meshDevice.getReceivedSize() + message.toBytes().length);
                meshHandlerCallback.onDeviceUpdated(meshDevice);
            } else {
                System.out.println("MeshHandler/onMessageReceived: Message is an echo " + message.getSource());
            }
        }
    }
}
