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
import jsl.modeling.Replication;


/**
 * Interface {@code MobilityPattern} is used to implement the <a
 * href="http://en.wikipedia.org/wiki/Mobility_model">mobility pattern</a> of
 * vertices.
 * <p>
 * {@code MobilityPattern} implementations can be designed so multiple vertices
 * could use the same instance. However in practice it is much easier to design
 * certain mobility patterns, especially those that are stateful across
 * waypoints, to be used by only one vertex. It is highly recommended that
 * developers of mobility patterns clearly specify in their documentation if
 * their mobility pattern is <em>sharable</em> or <em>not sharable</em>.
 * <p>
 * Each vertex upon addition to a {@link NetworkGraph networkGraph} is assigned
 * a {@code mobilityPattern}. The vertex <i>asks</i> the mobility pattern for a
 * {@link Waypoint waypoint} at the start of a {@link Replication replication},
 * and then repeats on asking for the next {@code waypoint} upon each waypoint
 * completion.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public interface MobilityPattern<V, E> {

    /**
     * Returns the next waypoint for {@code vertex}.
     * <p>
     * The waypoint's travel time must be larger then the current simulated
     * time.
     * <p>
     * {@code vertex} must be a member of {@code networkGraph}.
     *
     * @param networkGraph the networkgraph that contains vertex
     * @param vertex       the vertex requesting the waypoint
     * @return the next waypoint for the vertex
     */
    Waypoint<V, E> nextWaypoint(NetworkGraph<V, E> networkGraph, V vertex);

    Waypoint<V, E> getStartpoint(NetworkGraph<V, E> networkGraph, V vertex);

    void setStartpoint(Waypoint<V, E> startpoint);

    void nudge(double width, double height);
}
