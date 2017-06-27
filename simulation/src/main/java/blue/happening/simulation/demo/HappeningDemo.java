package blue.happening.simulation.demo;

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

        // create a custom blue.happening.bla.graph with Vertex: Device and Edge: Connection
        MeshGraph graph = new MeshGraph();

        // configuration
        final int deviceCount = 10;
        final int messageDelay = 100;
        final float messageLoss = 0.3f;
        final double speedMin = 0;
        final double speedMax = 0.5;
        final double width = 1000;
        final double height = 1000;

        // construct a bound; boundary of the canvas
        final RectangularBoundary<Device, Connection> bound = new RectangularBoundary<Device, Connection>(
                0, 0, width, height);

        // construct a random mobility pattern that conforms to that bound
        MobilityPattern<Device, Connection> mobilityPattern = new RandomDSMobilityPattern<Device, Connection>(
                bound, speedMin, speedMax);

        int deviceIndex = 1;
        int dimension = (int)Math.ceil(Math.sqrt(deviceCount));
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (deviceIndex <= deviceCount) {
                    Device device = new Device("Device_" + deviceIndex, graph);
                    device.setMessageDelay(messageDelay);
                    device.getMockLayer().setMessageLoss(messageLoss);
                    device.setTxRadius(100);
                    device.setRxRadius(100);
                    graph.addVertex(device, 100 + (i * 100), 100 + (j * 100), mobilityPattern, 0, 0);
                    deviceIndex++;
                }
            }
        }

        // Enable blue.happening.bla.visualization frame and panel
        MeshVisualizerFrame<Device, Connection> frame = new MeshVisualizerFrame<Device, Connection>(
                graph);

        // introduce noop events to slow down bla; blue.happening.bla.graph, interval, sleep
        new NOOPAction(graph, 1, 50);

        // create replication
        Replication replication = new Replication(graph.getModel());

        // set replication length
        replication.setLengthOfReplication(10000);

        // run replication
        replication.runAll();
    }
}
