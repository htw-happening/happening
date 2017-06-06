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

package blue.happening.simulation.mobility;

import java.util.Random;

import blue.happening.simulation.graph.NetworkGraph;


/**
 * This is a random mobility pattern that generates random {@link DSWaypoint}s
 * within predefined speed and displacement ranges. Configuration is done
 * through one of two constructors.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public final class RandomDSMobilityPattern<V, E>
        implements MobilityPattern<V, E> {

    private final double sxfMin;
    private final double sxfMax;
    private final double syfMin;
    private final double syfMax;
    private final double speedMin;
    private final double speedMax;

    private final Random random;

    /**
     * Constructs a new {@code RandomDSMobilityPattern} that will generate
     * {@code DSWaypoint}s with random final displacements within the limits of
     * {@link RectangularBoundary} {@code boundary}, and random speeds between
     * {@code speedMin} (inclusive) and {@code speedMax} (exclusive).
     * <p>
     * The random ranges are uniformly distributed.
     *
     * @param boundary a {@code RectangularBoundary} defining the limits of
     *                 displacement
     * @param speedMin minimum speed (inclusive)
     * @param speedMax maximum speed (exclusive)
     */
    public RandomDSMobilityPattern(final RectangularBoundary<V, E> boundary,
                                   final double speedMin, final double speedMax) {

        this(boundary.getX(), boundary.getX() + boundary.getWidth(),
                boundary.getY(), boundary.getY() + boundary.getHeight(), speedMin,
                speedMax);
    }

    /**
     * Constructs a new {@code RandomDSMobilityPattern} that will generate
     * {@code DSWaypoint}s with random x-axis final displacements between
     * {@code sxfMin} (inclusive) and {@code sxfMax} (exclusive), random y-axis
     * final displacements between {@code syfMin} (inclusive) and {@code syfMax}
     * (exclusive), and random speeds between {@code speedMin} (inclusive) and
     * {@code speedMax} (exclusive).
     * <p>
     * The random ranges are uniformly distributed.
     *
     * @param sxfMin   minimum x-axis displacement (inclusive)
     * @param sxfMax   maximum x-axis displacement (exclusive)
     * @param syfMin   minimum y-axis displacement (inclusive)
     * @param syfMax   maximum y-axis displacement (exclusive)
     * @param speedMin minimum speed (inclusive)
     * @param speedMax maximum speed (exclusive)
     */
    public RandomDSMobilityPattern(final double sxfMin, final double sxfMax,
                                   final double syfMin, final double syfMax, final double speedMin,
                                   final double speedMax) {
        this.sxfMin = sxfMin;
        this.sxfMax = sxfMax;
        this.syfMin = syfMin;
        this.syfMax = syfMax;
        this.speedMin = speedMin;
        this.speedMax = speedMax;

        this.random = new Random();
    }

    @Override
    public Waypoint<V, E> nextWaypoint(final NetworkGraph<V, E> networkGraph,
                                       final V vertex) {

        final double sxf = sxfMin + (random.nextDouble() * (sxfMax - sxfMin));
        final double syf = syfMin + (random.nextDouble() * (syfMax - syfMin));
        final double speed =
                speedMin + (random.nextDouble() * (speedMax - speedMin));

        return new DSWaypoint<V, E>(sxf, syf, speed);
    }

}
