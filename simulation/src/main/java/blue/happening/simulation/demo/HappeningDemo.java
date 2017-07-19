package blue.happening.simulation.demo;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import blue.happening.mesh.MeshHandler;
import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.internal.MeshGraph;
import blue.happening.simulation.visualization.MeshVisualizerFrame;
import blue.happening.simulation.visualization.NOOPAction;
import jsl.modeling.IterativeProcess;
import jsl.modeling.Replication;


@SuppressWarnings("WeakerAccess")
public abstract class HappeningDemo {

    int deviceCount;
    int messageDelay;
    int replicationLength;
    int warmUpLength;
    float messageLoss;
    double speedMin;
    double speedMax;
    double txRadius;
    double rxRadius;
    double minRadius;
    double noopInterval;
    long noopSleep;
    double repaintHz;

    final ScheduledExecutorService runner;
    final ScheduledExecutorService postman;
    private static NetworkGraph<Device, Connection> graph;
    private Replication replication;
    private MeshVisualizerFrame<Device, Connection> frame;

    HappeningDemo() {
        MeshHandler.INITIAL_MESSAGE_TQ = 255;
        MeshHandler.INITIAL_MESSAGE_TTL = 5;
        MeshHandler.HOP_PENALTY = 15;
        MeshHandler.OGM_INTERVAL = 4;
        MeshHandler.PURGE_INTERVAL = 12;
        MeshHandler.NETWORK_STAT_INTERVAL = 1;
        MeshHandler.SLIDING_WINDOW_SIZE = 12;
        MeshHandler.DEVICE_EXPIRATION = 16;
        MeshHandler.INITIAL_MIN_SEQUENCE = 0;
        MeshHandler.INITIAL_MAX_SEQUENCE = 1024;

        this.deviceCount = 50;
        this.messageDelay = 240;
        this.replicationLength = 5000;
        this.warmUpLength = 0;
        this.messageLoss = 0.0F;
        this.speedMin = 0.25D;
        this.speedMax = 2.0D;
        this.txRadius = 100D;
        this.rxRadius = 100D;
        this.noopInterval = 1D;
        this.noopSleep = 50L;
        this.repaintHz = 30D;

        minRadius = Math.min(txRadius, rxRadius);
        runner = Executors.newSingleThreadScheduledExecutor();
        postman = Executors.newSingleThreadScheduledExecutor();
    }

    abstract void populateGraph();

    private void createReplication() {
        replication = new Replication(graph.getModel());
        replication.setLengthOfReplication(replicationLength);
        replication.setLengthOfWarmUp(warmUpLength);
        replication.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object object) {
                IterativeProcess ip = (IterativeProcess) observable;
                if (ip.isEnded()) {
                    getPostman().shutdown();
                    getRunner().shutdown();
                }
            }
        });
    }

    public void start() {
        graph = new MeshGraph();
        frame = new MeshVisualizerFrame<>(graph, repaintHz);
        new NOOPAction(graph, noopInterval, noopSleep);
        populateGraph();
        createReplication();
        replication.runAll();
    }

    public void reset() {
        System.out.println("ended: " + replication.isEnded());
    }

    private ScheduledExecutorService getRunner() {
        return runner;
    }

    private ScheduledExecutorService getPostman() {
        return postman;
    }

    public static NetworkGraph<Device, Connection> getGraph() {
        return graph;
    }

    public MeshVisualizerFrame<Device, Connection> getFrame() {
        return frame;
    }

    public Replication getReplication() {
        return replication;
    }
}
