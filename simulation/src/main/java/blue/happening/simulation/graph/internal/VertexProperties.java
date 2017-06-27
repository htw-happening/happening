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

package blue.happening.simulation.graph.internal;

import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.mobility.MobilityPattern;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.Variable;


/**
 * This is the internal representation of vertices inside {@code NetworkGraph}.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class VertexProperties<V, E> extends SchedulingElement {

    private final NetworkGraph<V, E> graph;
    private final V vertex;

    // object variables
    private final Variable sx;
    private final Variable vx;

    private final Variable sy;
    private final Variable vy;

    private final Variable tStart;
    private final Variable tEnd;

    private final MobilityPattern<V, E> mobilityPattern;

    public VertexProperties(final NetworkGraph<V, E> graph, final String name,
                            final V vertex, final double sx, final double sy,
                            final MobilityPattern<V, E> mobilityPattern, final double txRadius,
                            final double rxRadius) {
        super(graph, name);

        this.graph = graph;
        this.vertex = vertex;

        // construct and set initial values of motion parameters
        this.sx = new Variable(this, sx);
        this.vx = new Variable(this);

        this.sy = new Variable(this, sy);
        this.vy = new Variable(this);

        this.tStart = new Variable(this);
        this.tEnd = new Variable(this);

        this.mobilityPattern = mobilityPattern;
    }

    @Override
    protected void initialize() {
        final VertexArrivalAction<V, E> action = new VertexArrivalAction<V, E>(
                this);
        super.initialize();
        scheduleEvent(action, 0);
    }

    public NetworkGraph<V, E> getNetworkGraph() {
        return graph;
    }

    public V getVertex() {
        return vertex;
    }

    public Variable getSx() {
        return sx;
    }

    public Variable getVx() {
        return vx;
    }

    public Variable getSy() {
        return sy;
    }

    public Variable getVy() {
        return vy;
    }

    public Variable getTStart() {
        return tStart;
    }

    public Variable getTEnd() {
        return tEnd;
    }

    public MobilityPattern<V, E> getMobilityPattern() {
        return mobilityPattern;
    }

    public double getTxRadius() {
        Device device = (Device) vertex;
        return device.getTxRadius();
    }

    public double getRxRadius() {
        Device device = (Device) vertex;
        return device.getRxRadius();
    }
}
