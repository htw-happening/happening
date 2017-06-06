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

import blue.happening.simulation.graph.NetworkGraph;


/**
 * A {@link Waypoint} implementation that is configured using a
 * <em>velocity and travel time</em>.
 * <p>
 * This class is immutable.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public final class VTWaypoint<V, E> implements Waypoint<V, E> {

    private final double vx;
    private final double vy;
    private final double travelTime;

    public VTWaypoint(final double vx, final double vy, final double travelTime) {
        this.vx = vx;
        this.vy = vy;
        this.travelTime = travelTime;
    }

    @Override
    public double getVelocityX(final NetworkGraph<V, E> networkGraph,
                               final V vertex) {
        return vx;
    }

    @Override
    public double getVelocityY(final NetworkGraph<V, E> networkGraph,
                               final V vertex) {
        return vy;
    }

    @Override
    public double getTravelTime(final NetworkGraph<V, E> networkGraph,
                                final V vertex) {
        return travelTime;
    }

}
