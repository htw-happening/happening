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

import java.util.List;

import blue.happening.simulation.graph.NetworkGraph;
import jsl.modeling.Replication;


/**
 * This mobility pattern allows to pre-configure a vertex with a path.
 * Configuration is done by passing a series of {@code Waypoint}s to the
 * constructor. The {@code Waypoint}s are executed in the same order they are
 * passed. They can be configured to infinitely repeat in a loop, or run once
 * and then make the vertex stationary for the rest of the {@link Replication
 * replication}.
 * <p>
 * This mobility pattern is not sharable. It must not be assigned to multiple
 * vertices.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class PredefinedMobilityPattern<V, E> implements MobilityPattern<V, E> {

    private final boolean repeat;
    private final List<Waypoint<V, E>> waypoints;
    private final MobilityPattern<V, E> fallbackPattern;
    private int index = 0;

    /**
     * Constructs a new {@code PredefinedMobilityPattern} with the given array
     * of {@code waypoints}. The {@code repeat} argument specifies whether to
     * repeat the {@code waypoints} in a loop, or run through them once and then
     * make the vertex stationary.
     *
     * @param repeat    set {@code true} to repeat, {@code false} otherwise
     * @param waypoints the set of {@code waypoint}s to run through
     */
    public PredefinedMobilityPattern(boolean repeat, List<Waypoint<V, E>> waypoints, MobilityPattern<V, E> fallbackPattern) {
        if (waypoints == null)
            throw new NullPointerException();
        if (waypoints.size() == 0)
            throw new IllegalArgumentException("At least one waypoint must be provided");
        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i) == null)
                throw new NullPointerException("Waypoint index " + i + " is null");
        }

        this.repeat = repeat;
        this.waypoints = waypoints;
        this.fallbackPattern = fallbackPattern;
    }

    @Override
    public Waypoint<V, E> nextWaypoint(NetworkGraph<V, E> networkGraph, V vertex) {
        if (index == waypoints.size()) {
            if (!repeat)
                return fallbackPattern.nextWaypoint(networkGraph, vertex);
            else
                index = 0;
        }
        index++;
        return waypoints.get(index - 1);
    }

    @Override
    public Waypoint<V, E> getStartpoint(NetworkGraph<V, E> networkGraph, V vertex) {
        try {
            return waypoints.get(0);
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    @Override
    public void nudge(double width, double height) {
        fallbackPattern.nudge(width, height);
    }
}
