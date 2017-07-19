package blue.happening.simulation.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.internal.MeshGraph;
import blue.happening.simulation.mobility.DTWaypoint;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.PredefinedMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.mobility.Waypoint;
import blue.happening.simulation.visualization.MeshVisualizerFrame;
import blue.happening.simulation.visualization.NOOPAction;
import jsl.modeling.Replication;


public class PredefinedMobilityDemo {

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

        // create a mesh runner executor service
        ScheduledExecutorService runner = Executors.newSingleThreadScheduledExecutor();

        // create message delivery executor service
        ScheduledExecutorService postman = Executors.newSingleThreadScheduledExecutor();

        // construct a bound; boundary of the canvas
        final RectangularBoundary<Device, Connection> bound = new RectangularBoundary<Device, Connection>(
                0, 0, width, height);

        // construct a random mobility pattern that conforms to that bound
        List<Waypoint<Device, Connection>> dtwaypoints0 = new ArrayList<>();
        dtwaypoints0.add(new DTWaypoint<Device, Connection>(200, 100, 1));
        dtwaypoints0.add(new DTWaypoint<Device, Connection>(200, 100, 1));
        MobilityPattern<Device, Connection> predefinedMobilityPattern0 = new PredefinedMobilityPattern<>(true, dtwaypoints0);

        List<Waypoint<Device, Connection>> dtwaypoints1 = new ArrayList<>();
        dtwaypoints1.add(new DTWaypoint<Device, Connection>(300, 100, 20));
        dtwaypoints1.add(new DTWaypoint<Device, Connection>(200, 100, 20));
        dtwaypoints1.add(new DTWaypoint<Device, Connection>(300, 100, 20));
        MobilityPattern<Device, Connection> predefinedMobilityPattern1 = new PredefinedMobilityPattern<>(true, dtwaypoints1);

        List<Waypoint<Device, Connection>> dtwaypoints2 = new ArrayList<>();
        dtwaypoints2.add(new DTWaypoint<Device, Connection>(400, 100, 80));
        dtwaypoints2.add(new DTWaypoint<Device, Connection>(200, 100, 80));
        MobilityPattern<Device, Connection> predefinedMobilityPattern2 = new PredefinedMobilityPattern<>(true, dtwaypoints2);

        List<Waypoint<Device, Connection>> dtwaypoints3 = new ArrayList<>();
        dtwaypoints3.add(new DTWaypoint<Device, Connection>(500, 100, 1));
        dtwaypoints3.add(new DTWaypoint<Device, Connection>(500, 100, 1));
        MobilityPattern<Device, Connection> predefinedMobilityPattern3 = new PredefinedMobilityPattern<>(true, dtwaypoints3);

        List<MobilityPattern<Device, Connection>> mobilityPatterns = new ArrayList<>();
        mobilityPatterns.add(predefinedMobilityPattern0);
        mobilityPatterns.add(predefinedMobilityPattern1);
        mobilityPatterns.add(predefinedMobilityPattern2);
        mobilityPatterns.add(predefinedMobilityPattern3);

        for (int i = 0; i < mobilityPatterns.size(); i++) {
            graph.addVertex(new Device("Test_" + i + "_" + i, graph, postman, runner, 100, 0),
                    100 + (i * 100), 100 + (i * 100), mobilityPatterns.get(i), txRadius, rxRadius);
        }

        // Enable blue.happening.simulation.visualization frame and panel
        MeshVisualizerFrame<Device, Connection> frame = new MeshVisualizerFrame<>(graph, 60D);

        // introduce noop events to slow down simulation; blue.happening.simulation.graph, interval, sleep
        new NOOPAction(graph, 1, 50);

        // create replication
        Replication replication = new Replication(graph.getModel());

        // set replication length
        replication.setLengthOfReplication(10000);

        // run replication
        replication.runAll();
    }
}
