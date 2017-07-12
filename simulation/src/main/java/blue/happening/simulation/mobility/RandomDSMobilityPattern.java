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
public final class RandomDSMobilityPattern<V, E> implements MobilityPattern<V, E> {

    private final double speedMin;
    private final double speedMax;
    private double nudge;

    private final Random random;
    private final RectangularBoundary<V, E> boundary;

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

        this.boundary = boundary;
        this.speedMin = speedMin;
        this.speedMax = speedMax;
        this.random = new Random();
    }

    public void nudge() {
        nudge += 1f;
    }

    public RectangularBoundary<V, E> getBoundary() {
        return boundary;
    }

    @Override
    public Waypoint<V, E> nextWaypoint(final NetworkGraph<V, E> networkGraph, final V vertex) {

        double sxfMin = boundary.getX();
        double sxfMax = boundary.getX() + boundary.getWidth();
        double syfMin = boundary.getY();
        double syfMax = boundary.getY() + boundary.getHeight();

        double sxf = sxfMin + (random.nextDouble() * (sxfMax - sxfMin));
        double syf = syfMin + (random.nextDouble() * (syfMax - syfMin));
        double speed = nudge + speedMin + random.nextDouble() * (speedMax + -speedMin);
        nudge = Math.max(0f, nudge - 1f);

        return new DSWaypoint<>(sxf, syf, speed);
    }

}
