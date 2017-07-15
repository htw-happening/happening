package blue.happening.simulation.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import blue.happening.mesh.MeshHandler;
import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.internal.MeshGraph;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.visualization.MeshVisualizerFrame;
import blue.happening.simulation.visualization.NOOPAction;
import jsl.modeling.Replication;


public class HappeningDemo {

    public static void main(String[] args) throws InterruptedException {

        // configuration
        final int deviceCount = 24;
        final int messageDelay = 320;
        final int replicationLength = 10000;
        final float messageLoss = 0.1F;
        final double speedMin = 0.5D;
        final double speedMax = 1.5D;
        final double txRadius = 100D;
        final double rxRadius = 100D;
        final double noopInterval = 1D;
        final long noopSleep = 50L;
        final double repaintHz = 30D;
        MeshHandler.INITIAL_MESSAGE_TQ = 255;
        MeshHandler.INITIAL_MESSAGE_TTL = 5;
        MeshHandler.HOP_PENALTY = 15;
        MeshHandler.OGM_INTERVAL = 5;
        MeshHandler.PURGE_INTERVAL = 50;
        MeshHandler.NETWORK_STAT_INTERVAL = 1;
        MeshHandler.SLIDING_WINDOW_SIZE = 12;
        MeshHandler.DEVICE_EXPIRATION = 20;

        // create a custom graph with Vertex: Device and Edge: Connection
        MeshGraph graph = new MeshGraph();

        // enable visualization frame and panel
        MeshVisualizerFrame<Device, Connection> frame = new MeshVisualizerFrame<>(graph, repaintHz);

        // create a mesh runner executor service
        ScheduledExecutorService runner = Executors.newSingleThreadScheduledExecutor();

        // create message delivery executor service
        ScheduledExecutorService postman = Executors.newSingleThreadScheduledExecutor();

        // initialize devices and place them on the in the scene
        int deviceIndex = 0;
        final double frameHeight = frame.getVisualizerPanel().getHeight();
        final double frameWidth = frame.getVisualizerPanel().getWidth();
        final int root = (int) Math.ceil(Math.sqrt(deviceCount));
        final double radius = Math.min(txRadius, rxRadius);
        final double verticalStep = Math.min(radius, frameHeight / (root + 1));
        final double horizontalStep = Math.min(radius, frameWidth / (root + 1));
        final double verticalPadding = (frameHeight - (verticalStep * (root - 1))) / 2;
        final double horizontalPadding = (frameWidth - (horizontalStep * (root - 1))) / 2;
        final RectangularBoundary<Device, Connection> bound = new RectangularBoundary<>(0, 0, frameWidth, frameHeight);

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

        // create replication
        Replication replication = new Replication(graph.getModel());

        // set replication length
        replication.setLengthOfReplication(replicationLength);

        // run replication
        replication.runAll();
    }
}
