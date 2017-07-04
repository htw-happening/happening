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

import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.RandomVTMobilityPattern;
import blue.happening.simulation.visualization.NOOPAction;
import blue.happening.simulation.visualization.SimpleVisualizerFrame;
import blue.happening.simulation.visualization.SimpleVisualizerPanel;
import jsl.modeling.Replication;


/**
 * This blue.happening.simulation.demo demonstrates an <em>unbounded</em> arena simulation. It creates a
 * bunch of vertices with a {@link RandomVTMobilityPattern
 * randomVTMobilityPattern}. These vertices have no arena bound and will
 * eventually travel to 'infinity'.
 * <p>
 * This is a GUI blue.happening.simulation.demo.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class RandomUnboundedDemo {

    public static void main(String[] args) throws InterruptedException {

        // create a blue.happening.simulation.graph
        StringStringNetworkGraph graph = new StringStringNetworkGraph();

        // configuration
        final int nVertices = 50;
        final double txRadius = 100;
        final double rxRadius = 1;
        final double vxMin = -5;
        final double vxMax = 5;
        final double vyMin = -5;
        final double vyMax = 5;
        final double travelTimeMin = 1;
        final double travelTimeMax = 60;
        final double initialX = 300;
        final double initialY = 300;

        // construct a random mobility pattern that conforms to that bound
        MobilityPattern<String, String> pattern = new RandomVTMobilityPattern<String, String>(
                vxMin, vxMax, vyMin, vyMax, travelTimeMin, travelTimeMax);

        // add vertices to blue.happening.simulation.graph at random coordinates
        for (int i = 0; i < nVertices; i++) {
            graph.addVertex(i + "", initialX, initialY, pattern, txRadius, rxRadius);
        }

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
