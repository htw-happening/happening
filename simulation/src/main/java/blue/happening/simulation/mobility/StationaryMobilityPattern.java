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
 * This is the stationary mobility pattern that makes a vertex not move.
 * <p>
 * This pattern works by assigning a {@link VTWaypoint} to the vertex at
 * {@link Replication replication} start, with infinite travel time and zero
 * velocity.
 * <p>
 * This mobility pattern is sharable. It can be assigned to multiple vertices.
 * <p>
 * This class is immutable.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public final class StationaryMobilityPattern<V, E>
        implements MobilityPattern<V, E> {

    private final Waypoint<V, E> stationaryWaypoint = new VTWaypoint<V, E>(0, 0,
            Double.POSITIVE_INFINITY);

    /**
     * Constructs a new {@code StationaryMobilityPattern} object.
     */
    public StationaryMobilityPattern() {
    }

    @Override
    public Waypoint<V, E> nextWaypoint(NetworkGraph<V, E> networkGraph,
                                       V vertex) {
        return stationaryWaypoint;
    }

}
