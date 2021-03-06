package blue.happening.mesh;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import blue.happening.mesh.statistics.NetworkStats;
import blue.happening.mesh.statistics.StatsResult;

public class MeshHandler {

    public static int INITIAL_MESSAGE_TQ = 255;
    public static int INITIAL_MESSAGE_TTL = 5;
    public static int HOP_PENALTY = 15;
    public static int OGM_INTERVAL = 2;
    public static int PURGE_INTERVAL = 200;
    public static int NETWORK_STAT_INTERVAL = 1;
    public static int SLIDING_WINDOW_SIZE = 12;
    public static int DEVICE_EXPIRATION = 200;
    public static int INITIAL_MIN_SEQUENCE = 0;
    public static int INITIAL_MAX_SEQUENCE = Integer.MAX_VALUE;

    public static final int MESSAGE_ACTION_ARRIVED = 0;
    public static final int MESSAGE_ACTION_RECEIVED = 1;
    public static final int MESSAGE_ACTION_DROPPED = 2;
    public static final int MESSAGE_ACTION_FORWARDED = 3;
    public static final int MESSAGE_ACTION_SENT = 4;

    public static final int MESSAGE_TYPE_OGM = 1;
    public static final int MESSAGE_TYPE_UCM = 2;
    static final String BROADCAST_ADDRESS = "BROADCAST";

    private final RoutingTable routingTable;
    private final Router router;
    private final ILayerCallback layerCallback;
    private final String uuid;
    private IMeshHandlerCallback meshHandlerCallback;
    private NetworkStats ucmStats;
    private NetworkStats ogmStats;

    public MeshHandler(String uuid) {
        this(uuid, Executors.newSingleThreadScheduledExecutor());
    }

    public MeshHandler(String uuid, ScheduledExecutorService executor) {
        this.uuid = uuid;
        routingTable = new RoutingTable();
        router = new Router(routingTable, uuid);
        layerCallback = new LayerCallback();
        ucmStats = new NetworkStats();
        ogmStats = new NetworkStats();

        double currentTime = System.currentTimeMillis();
        ucmStats.updateTs(currentTime);
        ogmStats.updateTs(currentTime);

        router.addObserver(new RouterObserver());

        executor.scheduleAtFixedRate(
                new OGMRunner(),
                ThreadLocalRandom.current().nextInt(OGM_INTERVAL),
                OGM_INTERVAL, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(
                new PurgeRunner(),
                ThreadLocalRandom.current().nextInt(PURGE_INTERVAL),
                PURGE_INTERVAL, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(
                new NetworkStatsUpdateRunner(),
                NETWORK_STAT_INTERVAL,
                NETWORK_STAT_INTERVAL, TimeUnit.SECONDS);
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
        RemoteDevice remoteDevice = routingTable.get(uuid);
        if (remoteDevice == null) {
            System.out.println("Mesh handler could not find " + uuid + " in routing table");
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
                router.dispatchOgm();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class PurgeRunner implements Runnable {
        @Override
        public void run() {
            try {
                routingTable.flushExpiredRemoteDevices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class NetworkStatsUpdateRunner implements Runnable {
        @Override
        public void run() {
            try {
                double currentTime = System.currentTimeMillis();
                StatsResult networkStat = new StatsResult();

                networkStat.setOgmIncoming(ogmStats.getIncomingStat());
                networkStat.setOgmOutgoing(ogmStats.getOutgoingStat());
                networkStat.setUcmIncoming(ucmStats.getIncomingStat());
                networkStat.setUcmOutgoing(ucmStats.getOutgoingStat());

                meshHandlerCallback.onNetworkStatsUpdated(networkStat);

                ucmStats.updateTs(currentTime);
                ogmStats.updateTs(currentTime);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RouterObserver implements Observer {
        @Override
        public void update(Observable observable, Object o) {
            Router.Event event = (Router.Event) o;
            Message message = (Message) event.getOptions();
            boolean sent = message.getSource().equals(uuid);
            switch (event.getType()) {
                case Router.OGM_SENT:
                    ogmStats.addOutGoingMessage((Message) event.getOptions());
                    meshHandlerCallback.onMessageLogged(message, sent ? MESSAGE_ACTION_SENT : MESSAGE_ACTION_FORWARDED);
                    break;
                case Router.UCM_SENT:
                    ucmStats.addOutGoingMessage(message);
                    meshHandlerCallback.onMessageLogged(message, sent ? MESSAGE_ACTION_SENT : MESSAGE_ACTION_FORWARDED);
                    break;
                case Router.OGM_DROPPED:
                    meshHandlerCallback.onMessageLogged(message, MESSAGE_ACTION_DROPPED);
                    break;
                case Router.UCM_DROPPED:
                    meshHandlerCallback.onMessageLogged(message, MESSAGE_ACTION_DROPPED);
                    break;

            }
        }
    }

    private class LayerCallback implements ILayerCallback {

        @Override
        public void onDeviceAdded(RemoteDevice remoteDevice) {
            routingTable.putRoute(remoteDevice, remoteDevice);
        }

        @Override
        public void onDeviceRemoved(RemoteDevice remoteDevice) {
            routingTable.removeRoutesVia(remoteDevice);
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
                System.out.println("Message broken: " + e.getMessage());
                return;
            }

            if (message.getType() == MESSAGE_TYPE_OGM) {
                ogmStats.addInComingMessage(message);
            } else if (message.getType() == MESSAGE_TYPE_UCM) {
                ucmStats.addInComingMessage(message);
            }

            meshHandlerCallback.onMessageLogged(message, MESSAGE_ACTION_ARRIVED);

            try {
                propagate = router.routeMessage(message);
            } catch (Router.RoutingException e) {
                System.out.println("Routing failed: " + e.getMessage());
                return;
            }

            if (propagate != null) {
                MeshDevice source = routingTable.get(message.getSource()).getMeshDevice();
                meshHandlerCallback.onMessageReceived(message.getBody(), source);
                meshHandlerCallback.onMessageLogged(message, MESSAGE_ACTION_RECEIVED);
            }

            // Check whether message is an echo OGM
            if (!message.getSource().equals(uuid)) {
                RemoteDevice source = routingTable.get(message.getSource());
                if (source != null) {
                    // TODO: Move this block to a better location
                    MeshDevice meshDevice = source.getMeshDevice();
                    meshDevice.setReceivedSize(meshDevice.getReceivedSize() + message.toBytes().length);
                    meshHandlerCallback.onDeviceUpdated(meshDevice);
                }
            }
        }
    }
}
