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
        final int deviceCount = 20;
        final int messageDelay = 500;
        final float messageLoss = 0.1f;
        final double speedMin = 0.0D;
        final double speedMax = 1.0D;
        final double width = 1000;
        final double txRadius = 100;
        final double rxRadius = 100;
        final double height = 1000;
        final int replicationLength = 10000;
        final double noopInterval = 1;
        final long noopSleep = 50;
        MeshHandler.INITIAL_MESSAGE_TQ = 255;
        MeshHandler.INITIAL_MESSAGE_TTL = 5;
        MeshHandler.HOP_PENALTY = 15;
        MeshHandler.OGM_INTERVAL = 5;
        MeshHandler.PURGE_INTERVAL = 200;
        MeshHandler.NETWORK_STAT_INTERVAL = 1;
        MeshHandler.SLIDING_WINDOW_SIZE = 12;
        MeshHandler.DEVICE_EXPIRATION = 200;

        // create a custom graph with Vertex: Device and Edge: Connection
        MeshGraph graph = new MeshGraph();

        // create message delivery executor service
        ScheduledExecutorService postman = Executors.newSingleThreadScheduledExecutor();

        // construct a bound; boundary of the canvas
        RectangularBoundary<Device, Connection> bound = new RectangularBoundary<>(0, 0, width, height);

        // construct a random mobility pattern that conforms to that bound
        MobilityPattern<Device, Connection> pattern = new RandomDSMobilityPattern<>(bound, speedMin, speedMax);

        // initialize devices and place them on the in the scene
        int deviceIndex = 0;
        int dimension = (int) Math.ceil(Math.sqrt(deviceCount));
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (deviceIndex < deviceCount) {
                    Device device = new Device("Device_" + deviceIndex, graph, postman);
                    device.setMessageDelay(messageDelay);
                    device.getMockLayer().setMessageLoss(messageLoss);
                    device.setTxRadius(txRadius);
                    device.setRxRadius(rxRadius);
                    graph.addVertex(device, 100 + (i * 100), 100 + (j * 100), pattern, 0, 0);
                    deviceIndex++;
                }
            }
        }

        // enable visualization frame and panel
        MeshVisualizerFrame<Device, Connection> frame = new MeshVisualizerFrame<>(graph);

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
