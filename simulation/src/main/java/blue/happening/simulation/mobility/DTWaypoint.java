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
import blue.happening.simulation.graph.internal.Motion;


/**
 * A {@link Waypoint} implementation that is configured using a
 * <em>final displacement and travel time</em>.
 * <p>
 * <p>
 * This class is immutable.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class DTWaypoint<V, E> implements Waypoint<V, E> {

    private final double sxf;
    private final double syf;
    private final double travelTime;

    public DTWaypoint(final double sxf, final double syf, final double travelTime) {
        this.sxf = sxf;
        this.syf = syf;
        this.travelTime = travelTime;
    }

    @Override
    public double getVelocityX(NetworkGraph<V, E> graph, V vertex) {
        final double sxi = graph.getEndDisplacementX(vertex);
        return Motion.solveForVelocity(sxi, sxf, travelTime);
    }

    @Override
    public double getVelocityY(NetworkGraph<V, E> graph, V vertex) {
        final double syi = graph.getEndDisplacementY(vertex);
        return Motion.solveForVelocity(syi, syf, travelTime);
    }

    @Override
    public double getTravelTime(NetworkGraph<V, E> graph, V vertex) {
        return travelTime;
    }

    public double getSxf() {
        return sxf;
    }

    public double getSyf() {
        return syf;
    }
}