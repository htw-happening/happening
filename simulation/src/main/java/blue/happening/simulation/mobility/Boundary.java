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
 * {@code Boundary} implementations represent various shapes of arenas.
 * Boundaries can be used by mobility patterns as building blocks for defining
 * two-dimensional spaces.
 * <p>
 * {@code Boundary} implementors must implement one method, {@code isInBoundary}.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public interface Boundary<V, E> {

    /**
     * Returns {@code true} if {@code vertex}, after moving to the destination
     * specified by {@code waypoint}, will be within the boundary. Otherwise
     * returns {@code false}.
     *
     * @param networkGraph the {@code networkgraph} that contains {@code vertex}
     * @param vertex       the vertex to test
     * @param waypoint     the waypoint to test
     * @return {@code true} if {@code vertex} will be within the boundary after
     * traveling to {@code waypoint}, otherwise {@code false}
     */
    public boolean isInBoundary(final NetworkGraph<V, E> networkGraph,
                                final V vertex, final Waypoint<V, E> waypoint);
}
