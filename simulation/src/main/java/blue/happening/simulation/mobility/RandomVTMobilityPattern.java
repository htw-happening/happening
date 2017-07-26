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
 * This is a random mobility pattern that generates random {@link VTWaypoint}s
 * within predefined x- and y-axis velocities and travel times. Configuration is
 * done through constructor.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public final class RandomVTMobilityPattern<V, E> implements MobilityPattern<V, E> {

    private final double vxMin;
    private final double vxMax;
    private final double vyMin;
    private final double vyMax;
    private final double travelTimeMin;
    private final double travelTimeMax;

    private final Random random;

    /**
     * Constructs a new {@code RandomVTMobilityPattern} that will generate
     * {@code VTWaypoint}s with random x-axis velocities between {@code vxMin}
     * (inclusive) and {@code vxMax} (exclusive), random y-axis velocities
     * between {@code vyMin} (inclusive) and {@code vyMax} (exclusive), and
     * random travel times between {@code travelTimeMin} (inclusive) and
     * {@code travelTimeMax} (exclusive).
     * <p>
     * The random ranges are uniformly distributed.
     *
     * @param vxMin         minimum x-axis velocity (inclusive)
     * @param vxMax         maximum x-axis velocity (exclusive)
     * @param vyMin         minimum y-axis velocity (inclusive)
     * @param vyMax         maximum y-axis velocity (exclusive)
     * @param travelTimeMin minimum travel time (inclusive)
     * @param travelTimeMax maximum travel time (exclusive)
     */
    public RandomVTMobilityPattern(final double vxMin, final double vxMax,
                                   final double vyMin, final double vyMax, final double travelTimeMin,
                                   final double travelTimeMax) {
        this.vxMin = vxMin;
        this.vxMax = vxMax;
        this.vyMin = vyMin;
        this.vyMax = vyMax;
        this.travelTimeMin = travelTimeMin;
        this.travelTimeMax = travelTimeMax;

        this.random = new Random();
    }

    @Override
    public Waypoint<V, E> nextWaypoint(final NetworkGraph<V, E> networkGraph,
                                       final V vertex) {

        final double vx = vxMin + (random.nextDouble() * (vxMax - vxMin));
        final double vy = vyMin + (random.nextDouble() * (vyMax - vyMin));
        final double travelTime =
                travelTimeMin + (random.nextDouble() * (travelTimeMax - travelTimeMin));

        return new VTWaypoint<V, E>(vx, vy, travelTime);
    }

    @Override
    public Waypoint<V, E> getStartpoint(NetworkGraph<V, E> networkGraph, V vertex) {
        return null;
    }
}
