package blue.happening.simulation.demo;

import java.util.ArrayList;
import java.util.List;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.internal.MeshGraph;
import blue.happening.simulation.mobility.DTWaypoint;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.PredefinedMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.visualization.MeshVisualizerFrame;
import blue.happening.simulation.visualization.NOOPAction;
import jsl.modeling.Replication;


public class HappeningPredefinedMobilityDemo {

    public static void main(String[] args) throws InterruptedException {

        // configuration
        final int nVertices = 100;
        final double txRadius = 100;
        final double rxRadius = 100;
        final double speedMin = 0;
        final double speedMax = 0;

        final double width = 1000;
        final double height = 1000;

        // create a custom graph with Vertex: Device and Edge: Connection
        MeshGraph graph = new MeshGraph();

        // construct a bound; boundary of the canvas
        final RectangularBoundary<Device, Connection> bound = new RectangularBoundary<Device, Connection>(
                0, 0, width, height);

        // construct a random mobility pattern that conforms to that bound
        @SuppressWarnings("rawtypes") DTWaypoint[] dtwaypoints = {
                new DTWaypoint<String, String>(200, 100, 1),
                new DTWaypoint<String, String>(200, 100, 1)};
        MobilityPattern<Device, Connection> predefinedMobilityPattern = new PredefinedMobilityPattern<Device, Connection>(
                true, dtwaypoints);

        @SuppressWarnings("rawtypes") DTWaypoint[] dtwaypoints1 = {
                new DTWaypoint<String, String>(300, 100, 20),
                new DTWaypoint<String, String>(200, 100, 20),
                new DTWaypoint<String, String>(300, 100, 20)};
        MobilityPattern<Device, Connection> predefinedMobilityPattern1 = new PredefinedMobilityPattern<Device, Connection>(
                true, dtwaypoints1);

        @SuppressWarnings("rawtypes") DTWaypoint[] dtwaypoints2 = {
                new DTWaypoint<String, String>(400, 100, 80),
                new DTWaypoint<String, String>(200, 100, 80)};
        MobilityPattern<Device, Connection> predefinedMobilityPattern2 = new PredefinedMobilityPattern<Device, Connection>(
                true, dtwaypoints2);

        @SuppressWarnings("rawtypes") DTWaypoint[] dtwaypoints3 = {
                new DTWaypoint<String, String>(500, 100, 1),
                new DTWaypoint<String, String>(500, 100, 1)};
        MobilityPattern<Device, Connection> predefinedMobilityPattern3 = new PredefinedMobilityPattern<Device, Connection>(
                true, dtwaypoints3);

        List<MobilityPattern<Device, Connection>> mobilityPatterns = new ArrayList();
        mobilityPatterns.add(predefinedMobilityPattern);
        mobilityPatterns.add(predefinedMobilityPattern1);
        mobilityPatterns.add(predefinedMobilityPattern2);
        mobilityPatterns.add(predefinedMobilityPattern3);

        for (int i = 0; i < mobilityPatterns.size(); i++) {
            graph.addVertex(new Device("Test_" + i + "_" + i, graph),
                    100 + (i * 100), 100 + (i * 100), mobilityPatterns.get(i), txRadius,
                    rxRadius);
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
