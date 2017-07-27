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
 * A {@code waypoint} is an instruction to a vertex of how and where to travel.
 * Upon arrival of a vertex at a waypoint, the vertex requests it's
 * <em>next</em> waypoint.
 * <p>
 * The waypoint tells the verex <em>where</em> to travel and <em>when</em> to
 * get there. For a programmer it is sometimes convenient to specify waypoints
 * as combinations of displacement and speed, displacement and time, velocity
 * and time, or some other combination. As a result, {@code Waypoint} is an
 * interface to multiple implementations which allow the programmer to specify a
 * waypoint in the most convenient manner for a given situation.
 * <p>
 * This package provides the following {@code Waypoint} implementations:
 * <ul>
 * <li>{@link DSWaypoint} - displacement/speed combination</li>
 * <li>{@link DTWaypoint} - displacement/time combination</li>
 * <li>{@link VTWaypoint} - velocity/time combination</li>
 * </ul>
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public interface Waypoint<V, E> {

    /**
     * Returns the velocity of the {@code vertex} on the x-axis.
     *
     * @param networkGraph the {@code networkgraph} that contains {@code vertex}
     * @param vertex       the requesting vertex
     * @return the x-axis velocity
     */
    double getVelocityX(NetworkGraph<V, E> networkGraph, V vertex);

    /**
     * Returns the velocity of the {@code vertex} on the y-axis.
     *
     * @param networkGraph the {@code networkgraph} that contains {@code vertex}
     * @param vertex       the requesting vertex
     * @return the y-axis velocity
     */
    double getVelocityY(NetworkGraph<V, E> networkGraph, V vertex);

    /**
     * Returns the travel time of the {@code vertex}.
     *
     * @param networkGraph the {@code networkgraph} that contains {@code vertex}
     * @param vertex       the requesting vertex
     * @return the travel time
     */
    double getTravelTime(NetworkGraph<V, E> networkGraph, V vertex);

    double getSxf();

    double getSyf();
}