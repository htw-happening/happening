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

package blue.happening.simulation.graph;

/**
 * An {@code EdgePool} implementation manages the edge objects of a
 * {@link NetworkGraph networkGraph} object. An instance of an {@code EdgePool}
 * implementation is required to construct an instance of {@code NetworkGraph}.
 * <p>
 * Also see the discussion on EdgePools in {@code NetworkGraph}'s class
 * documentation.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public interface EdgePool<V, E> {

    /**
     * Returns the edge that is incident from {@code fromVertex} and incident to
     * {@code toVertex}.
     * <p>
     * The same combination of arguments to {@code getEdge} <em>must</em> always
     * return objects that are equivalent as defined by {@code equals}. In other
     * words, multiple invocations of {@code getEdge} with the same argument
     * combination must not necessarily return the same instance of an object,
     * but they must return objects that are <em>equal</em> to each other.
     * <p>
     * Remember that all edges are directional, so a call to
     * {@code getEdge(v1, v2)} and {@code getEdge(v2, v1)} must not return the
     * same edge.
     *
     * @param fromVertex the vertex at which the edge originates
     * @param toVertex   the vertex at which the edge terminals
     * @return the edge that is incident from {@code fromVertex} and incident to
     * {@code toVertex}.
     * @see NetworkGraph
     */
    E getEdge(V fromVertex, V toVertex);
}
