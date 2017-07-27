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
 * <em>final displacement and speed</em>.
 * <p>
 * <p>
 * This class is immutable.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public final class DSWaypoint<V, E> implements Waypoint<V, E> {

    private final double sxf;
    private final double syf;
    private final double speed;

    public DSWaypoint(final double sxf, final double syf, final double speed) {
        this.sxf = sxf;
        this.syf = syf;
        this.speed = speed;
    }

    @Override
    public double getVelocityX(final NetworkGraph<V, E> graph, final V vertex) {
        final double sxi = graph.getEndDisplacementX(vertex);
        final double t = getTravelTime(graph, vertex);
        return (t == 0) ? 0 : Motion.solveForVelocity(sxi, sxf, t);
    }

    @Override
    public double getVelocityY(final NetworkGraph<V, E> graph, final V vertex) {
        final double syi = graph.getEndDisplacementY(vertex);
        final double t = getTravelTime(graph, vertex);
        return (t == 0) ? 0 : Motion.solveForVelocity(syi, syf, t);
    }

    @Override
    public double getTravelTime(final NetworkGraph<V, E> graph, final V vertex) {

        final double sxi = graph.getDisplacementX(vertex);
        final double syi = graph.getDisplacementY(vertex);
        final double x = sxf - sxi;
        final double y = syf - syi;
        final double distance = Math.hypot(x, y);

        return distance / speed;
    }

    @Override
    public double getSxf() {
        return 0;
    }

    @Override
    public double getSyf() {
        return 0;
    }
}
