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

import java.io.IOException;
import java.util.Random;

import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.statistic.EdgeCountStatistician;
import blue.happening.simulation.statistic.EdgeCreationRateStatistician;
import blue.happening.simulation.statistic.EdgeDurationStatistician;
import jsl.modeling.Experiment;


/**
 * This is an example of a variable-varying experiment. This is an experiment to
 * see how changing some variable about the MANET, such as the number of
 * vertices or the arena size affects it.
 * <p>
 * Running this will output CSV formated data that can easily be plotted.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class VaryingVariableDemo {

    public static void main(String[] args) throws IOException {
        /*
         * Enable one of these. Each varies a differnt variables: the number of
		 * vertices, the speed, the radii, the arena size, and all of the above.
		 */
        // varyingVertices();
        varyingSpeed();
        // varyingRadius();
        // varyingArena();
        // varyingEverything();
    }

    public static void varyingVertices() {

        // fixed simulation parameters
        final double speed = 10;
        final double radius = 100;
        final double width = 10000;
        final double height = 10000;
        final double replicationLength = 1000;
        final int nReplications = 10;

        // write simulation parameters to file
        System.out.println("# speed \t= " + speed);
        System.out.println("# radius \t= " + radius);
        System.out.println("# width \t= " + width);
        System.out.println("# height \t= " + height);
        System.out.println("#");
        System.out.println("# Replication Length \t\t= " + replicationLength);
        System.out.println("# Number of Replications \t= " + nReplications);
        System.out.println("#");
        System.out.println("# vertices,edge count,edge rate,edge duration");

        for (int vertices = 1; vertices <= 100; vertices += 1) {

            ManetExperiment experiment1 = new ManetExperiment(vertices, radius,
                    radius, speed, speed, width, height);
            experiment1.setLengthOfReplication(replicationLength);
            experiment1.setNumberOfReplications(nReplications);
            experiment1.runAll();

            final double edgeCount = experiment1.getAverageEdgeCount();
            final double edgeRate = experiment1.getAverageEdgeRate();
            final double edgeDuration = experiment1.getAverageEdgeDuration();

            System.out.println(
                    vertices + "," + edgeCount + "," + edgeRate + "," + edgeDuration);
        }
    }

    public static void varyingSpeed() {

        // fixed simulation parameters
        final int vertices = 10;
        final double radius = 100;
        final double width = 10000;
        final double height = 10000;
        final double replicationLength = 1000;
        final int nReplications = 10;

        // write simulation parameters to file
        System.out.println("# vertices \t= " + vertices);
        System.out.println("# radius \t= " + radius);
        System.out.println("# width \t= " + width);
        System.out.println("# height \t= " + height);
        System.out.println("#");
        System.out.println("# Replication Length \t\t= " + replicationLength);
        System.out.println("# Number of Replications \t= " + nReplications);
        System.out.println("#");
        System.out.println("# speed,edge count,edge rate,edge duration");

        for (int speed = 1; speed <= 100; speed += 1) {

            ManetExperiment experiment = new ManetExperiment(vertices, radius, radius,
                    speed, speed, width, height);

            experiment.setLengthOfReplication(replicationLength);
            experiment.setNumberOfReplications(nReplications);
            experiment.runAll();

            final double edgeCount = experiment.getAverageEdgeCount();
            final double edgeRate = experiment.getAverageEdgeRate();
            final double edgeDuration = experiment.getAverageEdgeDuration();

            System.out.println(
                    speed + "," + edgeCount + "," + edgeRate + "," + edgeDuration);
        }
    }

    public static void varyingRadius() {

        // fixed simulation parameters
        final int vertices = 10;
        final double speed = 100;
        final double width = 10000;
        final double height = 10000;
        final double replicationLength = 1000;
        final int nReplications = 10;

        // write simulation parameters to file
        System.out.println("# vertices \t= " + vertices);
        System.out.println("# speed \t= " + speed);
        System.out.println("# width \t= " + width);
        System.out.println("# height \t= " + height);
        System.out.println("#");
        System.out.println("# Replication Length \t\t= " + replicationLength);
        System.out.println("# Number of Replications \t= " + nReplications);
        System.out.println("#");
        System.out.println("# radius,edge count,edge rate,edge duration");

        for (int radius = 1; radius <= 100; radius += 1) {

            ManetExperiment experiment1 = new ManetExperiment(vertices, radius,
                    radius, speed, speed, width, height);
            experiment1.setLengthOfReplication(replicationLength);
            experiment1.setNumberOfReplications(nReplications);
            experiment1.runAll();

            final double edgeCount = experiment1.getAverageEdgeCount();
            final double edgeRate = experiment1.getAverageEdgeRate();
            final double edgeDuration = experiment1.getAverageEdgeDuration();

            System.out.println(
                    radius + "," + edgeCount + "," + edgeRate + "," + edgeDuration);
        }
    }

    public static void varyingArena() {

        // fixed simulation parameters
        final int vertices = 10;
        final double speed = 10;
        final double radius = 10;
        final double replicationLength = 1000;
        final int nReplications = 10;

        // write simulation parameters to file
        System.out.println("# vertices \t= " + vertices);
        System.out.println("# speed \t= " + speed);
        System.out.println("# radius \t= " + radius);
        System.out.println("#");
        System.out.println("# Replication Length \t\t= " + replicationLength);
        System.out.println("# Number of Replications \t= " + nReplications);
        System.out.println("#");
        System.out
                .println("# width (and height),edge count,edge rate,edge duration");

        for (int width = 10; width <= 1000; width += 10) {

            ManetExperiment experiment1 = new ManetExperiment(vertices, radius,
                    radius, speed, speed, width, width);
            experiment1.setLengthOfReplication(replicationLength);
            experiment1.setNumberOfReplications(nReplications);
            experiment1.runAll();

            final double edgeCount = experiment1.getAverageEdgeCount();
            final double edgeRate = experiment1.getAverageEdgeRate();
            final double edgeDuration = experiment1.getAverageEdgeDuration();

            System.out.println(
                    width + "," + edgeCount + "," + edgeRate + "," + edgeDuration);
        }
    }

    public static void varyingEverything() {

        // fixed simulation parameters
        // final int vertices = 10;
        // final double speed = 100;
        // final double width = 10000;
        // final double height = 10000;
        final double replicationLength = 1000;
        final int nReplications = 10;

        // write simulation parameters to file

        final long startTime = System.currentTimeMillis();
        System.out.println("#");
        System.out.println("# Replication Length \t\t= " + replicationLength);
        System.out.println("# Number of Replications \t= " + nReplications);
        System.out.println("#");
        System.out.println(
                "# vertices, radius, speed, width, edge count,edge rate,edge duration");

        for (int vertices = 100; vertices >= 10; vertices -= 10) {
            for (int radius = 100; radius >= 100; radius -= 10) {
                for (double speed = 100; speed >= 10; speed -= 10) {
                    for (double width = 1000; width >= 100; width -= 100) {

                        ManetExperiment experiment = new ManetExperiment(vertices, radius,
                                radius, speed, speed, width, width);

                        experiment.setLengthOfReplication(replicationLength);
                        experiment.setNumberOfReplications(nReplications);
                        experiment.runAll();

                        final double edgeCount = experiment.getAverageEdgeCount();
                        final double edgeRate = experiment.getAverageEdgeRate();
                        final double edgeDuration = experiment.getAverageEdgeDuration();

                        System.out.println(
                                vertices + "," + radius + "," + speed + "," + width + ","
                                        + edgeCount + "," + edgeRate + "," + edgeDuration);
                    }
                }
            }
        }

        final long endTime = System.currentTimeMillis();
        final float runtime = (endTime - startTime) / 1000;
        System.out.println("# Runtime: " + runtime + " seconds");
    }

    private static class ManetExperiment extends Experiment {

        private final EdgeCountStatistician<String, String> edgeCount;
        private final EdgeCreationRateStatistician<String, String> edgeRate;
        private final EdgeDurationStatistician<String, String> edgeDuration;

        public ManetExperiment(final int nVertices, final double txRadius,
                               final double rxRadius, final double speedMin, final double speedMax,
                               final double width, final double height) {

            super();

            final StringStringNetworkGraph graph = new StringStringNetworkGraph(
                    getModel());

            final RectangularBoundary<String, String> bound = new RectangularBoundary<String, String>(
                    0, 0, width, height);
            MobilityPattern<String, String> pattern = new RandomDSMobilityPattern<String, String>(
                    bound, speedMin, speedMax);
            final Random random = new Random();
            for (int i = 0; i < nVertices; i++) {
                final double initialX =
                        bound.getX() + (random.nextDouble() * (bound.getWidth()));
                final double initialY =
                        bound.getY() + (random.nextDouble() * (bound.getHeight()));
                graph
                        .addVertex("" + i, initialX, initialY, pattern, txRadius, rxRadius);
            }

            edgeCount = new EdgeCountStatistician<String, String>(graph);
            edgeRate = new EdgeCreationRateStatistician<String, String>(graph);
            edgeDuration = new EdgeDurationStatistician<String, String>(graph);

            // set experimental settings
            setSaveExperimentStatisticsOption(true);
            setSaveReplicationStatisticOption(true);
        }

        public double getAverageEdgeCount() {
            return edgeCount.getAcrossReplicationAverageEdgeCount();
        }

        public double getAverageEdgeRate() {
            return edgeRate.getAcrossReplicationAverageRate();
        }

        public double getAverageEdgeDuration() {
            return edgeDuration.getAcrossReplicationAverageEdgeDuration();
        }

    }

}