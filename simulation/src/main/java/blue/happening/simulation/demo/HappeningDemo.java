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
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.visualization.MeshVisualizerFrame;
import blue.happening.simulation.visualization.NOOPAction;
import jsl.modeling.IterativeProcess;
import jsl.modeling.Replication;


public class HappeningDemo {

    // configuration
    int deviceCount;
    int messageDelay;
    int replicationLength;
    float messageLoss;
    double speedMin;
    double speedMax;
    double txRadius;
    double rxRadius;
    double noopInterval;
    long noopSleep;
    double repaintHz;

    final ScheduledExecutorService runner;
    final ScheduledExecutorService postman;
    private NetworkGraph<Device, Connection> graph;
    private Replication replication;

    private static HappeningDemo instance;
    private boolean reset;
    private boolean pause;


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

        this.deviceCount = 10;
        this.messageDelay = 240;
        this.replicationLength = Integer.MAX_VALUE;
        this.messageLoss = 0.0F;
        this.speedMin = 0.25D;
        this.speedMax = 2.0D;
        this.txRadius = 100D;
        this.rxRadius = 100D;
        this.noopInterval = 1D;
        this.noopSleep = 50L;
        this.repaintHz = 30D;

        this.pause = false;

        // create a mesh runner executor service
        runner = Executors.newSingleThreadScheduledExecutor();

        // create message delivery executor service
        postman = Executors.newSingleThreadScheduledExecutor();
    }

    public static HappeningDemo getInstance() {
        if (instance == null) {
            instance = new HappeningDemo();
        }
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        getInstance().start();
    }

    NetworkGraph<Device, Connection> createGraph() {
        // create a custom graph with Vertex: Device and Edge: Connection
        MeshGraph graph = new MeshGraph();

        // enable visualization frame and panel
        MeshVisualizerFrame<Device, Connection> frame = new MeshVisualizerFrame<>(graph, repaintHz);

        // initialize devices and place them on the in the scene

        final double frameHeight = frame.getVisualizerPanel().getHeight();
        final double frameWidth = frame.getVisualizerPanel().getWidth();
        final int root = (int) Math.ceil(Math.sqrt(deviceCount));
        final double radius = Math.min(txRadius, rxRadius);
        final double verticalStep = Math.min(radius, frameHeight / (root + 1));
        final double horizontalStep = Math.min(radius, frameWidth / (root + 1));
        final double verticalPadding = (frameHeight - (verticalStep * (root - 1))) / 2;
        final double horizontalPadding = (frameWidth - (horizontalStep * (root - 1))) / 2;
        final RectangularBoundary<Device, Connection> bound = new RectangularBoundary<>(0, 0, frameWidth, frameHeight);

        int deviceIndex = 0;
        for (int i = 0; i < root; i++) {
            for (int j = 0; j < root; j++) {
                if (deviceIndex < deviceCount) {
                    Device device = new Device("Device_" + deviceIndex, graph, postman, runner);
                    device.setMessageDelay(messageDelay);
                    device.setMessageLoss(messageLoss);
                    double sx = horizontalPadding + (i * horizontalStep);
                    double sy = verticalPadding + (j * verticalStep);
                    MobilityPattern<Device, Connection> pattern = new RandomDSMobilityPattern<>(bound, speedMin, speedMax);
                    graph.addVertex(device, sx, sy, pattern, txRadius, rxRadius);
                    deviceIndex++;
                }
            }
        }

        // introduce noop events to slow down simulation
        new NOOPAction(graph, noopInterval, noopSleep);

        return graph;
    }

    private Replication createReplication(final NetworkGraph<Device, Connection> graph) {
        Replication replication = new Replication(graph.getModel());
        replication.setLengthOfReplication(replicationLength);
        replication.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object object) {
                IterativeProcess ip = (IterativeProcess) observable;
                if (ip.isRunning() && pause) {
                    while (pause) {
                        System.out.println("pause");
                    }
                }
                if (ip.isRunning() && reset) {
                    reset = false;
                    ip.end();
                    ip.initialize();
                }
                if (ip.isEnded()) {
                    getInstance().getPostman().shutdownNow();
                    getInstance().getRunner().shutdownNow();
                }
            }
        });
        return replication;
    }

    private void start() {
        graph = createGraph();
        replication = createReplication(graph);
        replication.runAll();
    }

    public void reset() {
        pause = false;
        reset = true;
    }

    public void pause() {
        pause = !pause;
    }

    private ScheduledExecutorService getRunner() {
        return runner;
    }

    private ScheduledExecutorService getPostman() {
        return postman;
    }

    public NetworkGraph<Device, Connection> getGraph() {
        return graph;
    }

}
