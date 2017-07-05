package blue.happening.simulation.demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

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
        final int deviceCount = 16;
        final int messageDelay = 500;
        final float delayVariance = 0.1f;
        final float messageLoss = 0.3f;
        final double speedMin = 0.0D;
        final double speedMax = 0.0D;
        final double width = 1000;
        final double height = 1000;
        MeshHandler.INITIAL_MESSAGE_TQ = 255;
        MeshHandler.INITIAL_MESSAGE_TTL = 5;
        MeshHandler.HOP_PENALTY = 15;
        MeshHandler.OGM_INTERVAL = 5;
        MeshHandler.PURGE_INTERVAL = 200;
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
                    int min = Math.round(messageDelay * (1 - delayVariance));
                    int max = Math.round(messageDelay * (1 + delayVariance));
                    int delay = ThreadLocalRandom.current().nextInt(min, max);
                    device.setMessageDelay(delay);
                    device.getMockLayer().setMessageLoss(messageLoss);
                    device.setTxRadius(100);
                    device.setRxRadius(100);
                    graph.addVertex(device, 100 + (i * 100), 100 + (j * 100), pattern, 0, 0);
                    deviceIndex++;
                }
            }
        }

        // enable visualization frame and panel
        MeshVisualizerFrame<Device, Connection> frame = new MeshVisualizerFrame<>(graph);

        // introduce noop events to slow down simulation
        new NOOPAction(graph, 1, 50);

        // create replication
        Replication replication = new Replication(graph.getModel());

        // set replication length
        replication.setLengthOfReplication(10000);

        // run replication
        replication.runAll();
    }
}
