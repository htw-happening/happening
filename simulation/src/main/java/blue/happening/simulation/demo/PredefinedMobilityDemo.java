/*
 * ManetSim - http://www.pages.drexel.edu/~sf69/sim.html
 * 
 * Copyright (C) 2010  Semyon Fishman
 * 
 * This file is part of ManetSim.
 * 
 * ManetSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ManetSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ManetSim.  If not, see <http://www.gnu.org/licenses/>.
 */

package blue.happening.simulation.demo;

import blue.happening.simulation.mobility.DSWaypoint;
import blue.happening.simulation.mobility.DTWaypoint;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.PredefinedMobilityPattern;
import blue.happening.simulation.mobility.VTWaypoint;
import blue.happening.simulation.mobility.Waypoint;
import blue.happening.simulation.visualization.NOOPAction;
import blue.happening.simulation.visualization.SimpleVisualizerFrame;
import blue.happening.simulation.visualization.SimpleVisualizerPanel;
import jsl.modeling.Replication;


/**
 * This blue.happening.simulation.demo demonstrates how to code predefined mobility patterns using
 * different {@link Waypoint} implementations: {@link DSWaypoint},
 * {@link DTWaypoint}, and {@link VTWaypoint}.
 * <p>
 * This is a GUI blue.happening.simulation.demo.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class PredefinedMobilityDemo {

    @SuppressWarnings("unchecked")
    public static void main(String[] args)
            throws InterruptedException {

        // create a blue.happening.simulation.graph
        StringStringNetworkGraph graph = new StringStringNetworkGraph();

        // radii configurations
        final double tx = 100;
        final double rx = 0;

		/*
         * Below are three examples of how to code the same exact predefine
		 * mobility pattern but using different waypoint implementations
		 */

        // predefined mobility pattern using displacement/dime waypoints
        @SuppressWarnings("rawtypes") DTWaypoint[] dtwaypoints = {
                new DTWaypoint<String, String>(300, 100, 100),
                new DTWaypoint<String, String>(300, 300, 100),
                new DTWaypoint<String, String>(100, 300, 100),
                new DTWaypoint<String, String>(100, 100, 100)};

        MobilityPattern<String, String> dtmp = new PredefinedMobilityPattern<String, String>(
                true, dtwaypoints);
        graph.addVertex("DT", 100, 100, dtmp, tx, rx);

        // predefined mobility pattern using displacement/speed waypoints
        @SuppressWarnings("rawtypes") Waypoint[] dswaypoints = {
                new DSWaypoint<String, String>(300, 400 + 100, 2),
                new DSWaypoint<String, String>(300, 400 + 300, 2),
                new DSWaypoint<String, String>(100, 400 + 300, 2),
                new DSWaypoint<String, String>(100, 400 + 100, 2)};

        MobilityPattern<String, String> dsmp = new PredefinedMobilityPattern<String, String>(
                true, dswaypoints);
        graph.addVertex("DS", 100, 500, dsmp, tx, rx);

        // predefined mobility pattern using velocity/time waypoints
        @SuppressWarnings("rawtypes") Waypoint[] vtwaypoints = {
                new VTWaypoint<String, String>(2, 0, 100),
                new VTWaypoint<String, String>(0, 2, 100),
                new VTWaypoint<String, String>(-2, 0, 100),
                new VTWaypoint<String, String>(0, -2, 100)};

        MobilityPattern<String, String> vtmp = new PredefinedMobilityPattern<String, String>(
                true, vtwaypoints);
        graph.addVertex("VT", 100, 900, vtmp, tx, rx);

        // Enable blue.happening.simulation.visualization
        SimpleVisualizerFrame<String, String> frame = new SimpleVisualizerFrame<String, String>(
                graph);
        @SuppressWarnings("unused")
        SimpleVisualizerPanel<String, String> panel = frame.getVisualizerPanel();

        // introduce noop events to slow down simulation
        new NOOPAction(graph, 1, 10);

        // create replication
        Replication replication = new Replication(graph.getModel());

        // set replication length
        replication.setLengthOfReplication(10000);

        // run replication
        replication.runAll();
    }
}
