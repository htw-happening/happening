package blue.happening.mesh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class MeshHandler {

    public static final long DEVICE_EXPIRATION_DURATION = 200;
    public static final int OGM_INTERVAL = 10;
    public static final int PURGE_INTERVAL = 2;
    public static final int HOP_PENALTY = 15;
    public static final String BROADCAST_ADDRESS = "broadcast";
    private static Logger logger = LogManager.getLogger(MeshHandler.class);

    private final RoutingTable routingTable;
    private final Router router;
    private final ILayerCallback layerCallback;
    private final String uuid;
    private IMeshHandlerCallback meshHandlerCallback; // TODO: should be list
    private int sequence;

    public MeshHandler(String uuid) {
        this.uuid = uuid;
        // sequence = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        sequence = ThreadLocalRandom.current().nextInt(512);
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

    public List<String> getDevices() {
        return new ArrayList<String>(routingTable.keySet());
    }

    private class OGMRunner implements Runnable {
        @Override
        public void run() {
            try {
                for (RemoteDevice remoteDevice : routingTable.getNeighbours()) {
                    Message message = new Message(uuid, BROADCAST_ADDRESS, ++sequence, Message.MESSAGE_TYPE_OGM, null);
                    logger.debug(uuid + " OGM SENT: " + message);
                    remoteDevice.sendMessage(message);
                }
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
            logger.debug(uuid + " DEVICE ADDED: " + remoteDevice);
            routingTable.ensureConnection(remoteDevice, remoteDevice);
            meshHandlerCallback.onDeviceAdded(remoteDevice.getUuid());
        }

        @Override
        public void onDeviceRemoved(RemoteDevice remoteDevice) {
            logger.debug(uuid + " DEVICE REMOVED: " + remoteDevice);
            routingTable.removeFromNeighbours(remoteDevice.getUuid());
        }

        @Override
        public void onMessageReceived(byte[] bytes) {
            Message message;

            try {
                message = Message.fromBytes(bytes);
                assert message != null;
            } catch (Exception e) {
                logger.debug(uuid + " MESSAGE BROKEN: " + e.getMessage());
                return;
            }
            try {
                message = router.routeMessage(message);
            } catch (Router.RoutingException e) {
                logger.debug(uuid + " ROUTING FAILED: " + e.getMessage());
                return;
            }

            if (message != null) {
                meshHandlerCallback.onMessageReceived(message.getBody());
            }
        }
    }
}
