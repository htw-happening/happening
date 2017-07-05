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

import java.util.Random;

import blue.happening.simulation.graph.internal.StringStringNetworkGraph;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.visualization.NOOPAction;
import blue.happening.simulation.visualization.SimpleVisualizerFrame;
import blue.happening.simulation.visualization.SimpleVisualizerPanel;
import jsl.modeling.Replication;


/**
 * This blue.happening.simulation.demo demonstrates a <em>bounded</em> arena simulation. It creates a
 * bunch of vertices with a {@link RandomDSMobilityPattern
 * randomDSMobilityPattern} that bounds them in a fixed rectangular arena.
 * <p>
 * This is a GUI blue.happening.simulation.demo.
 * <p>
 * Note, when the GUI first appears you won't see the vertices because they will
 * be in the lower right corner. You should zoom out till you see them and then
 * zoom in on them. Using your mouse scroll wheel is very convenient here.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class RandomBoundedDemo {

    public static void main(String[] args) throws InterruptedException {

        // create a blue.happening.simulation.graph
        StringStringNetworkGraph graph = new StringStringNetworkGraph();

        // configuration
        final int nVertices = 100;
        final double txRadius = 1000;
        final double rxRadius = 1;
        final double speedMin = 1;
        final double speedMax = 10;
        final double width = 10000; // huge arena, so make sure you zoom out
        final double height = 10000;

        // construct a bound
        final RectangularBoundary<String, String> bound = new RectangularBoundary<String, String>(
                0, 0, width, height);

        // construct a random mobility pattern that conforms to that bound
        MobilityPattern<String, String> pattern = new RandomDSMobilityPattern<String, String>(
                bound, speedMin, speedMax);

        // add vertices to blue.happening.simulation.graph at random coordinates
        final Random random = new Random();
        for (int i = 0; i < nVertices; i++) {
            final double initialX =
                    bound.getX() + (random.nextDouble() * (bound.getWidth()));
            final double initialY =
                    bound.getY() + (random.nextDouble() * (bound.getHeight()));
            graph.addVertex("" + i, initialX, initialY, pattern, txRadius, rxRadius);
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
