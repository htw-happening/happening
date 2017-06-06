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

import java.util.Collection;

import blue.happening.simulation.graph.NetworkGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;


/**
 * {@code AbstractSchedulingElementGraph} is a template class that glues the <a
 * href=
 * "http://www.uark.edu/~rossetti/research/research_interests/simulation/java_simulation_library_jsl/"
 * >Java HappeningDemo Library</a> (JSL) and the <a
 * href=http://jung.sourceforge.net/>Java Universal Network/Graph Framework</a>
 * (JUNG) together. It extends JSL's {@link SchedulingElement} and implements
 * JUNG's {@link Graph}. It is used as a decorator around the {@code Graph}
 * class that requires subclasses to pass the target {@code Graph} object upon
 * construction.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 * @see NetworkGraph
 */
public abstract class AbstractSchedulingElementGraph<V, E>
        extends SchedulingElement implements Graph<V, E> {

    protected Graph<V, E> delegate;

    public AbstractSchedulingElementGraph(ModelElement parent,
                                          Graph<V, E> delegate) {
        super(parent);
        this.delegate = delegate;
    }

    public AbstractSchedulingElementGraph(ModelElement parent, String name,
                                          Graph<V, E> delegate) {
        super(parent, name);
        this.delegate = delegate;
    }

    @Override
    public boolean addEdge(E edge, Collection<? extends V> vertices) {
        return delegate.addEdge(edge, vertices);
    }

    @Override
    public boolean addEdge(E edge, Collection<? extends V> vertices,
                           EdgeType edge_type) {
        return delegate.addEdge(edge, vertices, edge_type);
    }

    @Override
    public boolean addEdge(E e, V v1, V v2, EdgeType edgeType) {
        return delegate.addEdge(e, v1, v2, edgeType);
    }

    @Override
    public boolean addEdge(E e, V v1, V v2) {
        return delegate.addEdge(e, v1, v2);
    }

    @Override
    public boolean addVertex(V vertex) {
        return delegate.addVertex(vertex);
    }

    @Override
    public boolean isIncident(V vertex, E edge) {
        return delegate.isIncident(vertex, edge);
    }

    @Override
    public boolean isNeighbor(V v1, V v2) {
        return delegate.isNeighbor(v1, v2);
    }

    @Override
    public int degree(V vertex) {
        return delegate.degree(vertex);
    }

    @Override
    public E findEdge(V v1, V v2) {
        return delegate.findEdge(v1, v2);
    }

    @Override
    public Collection<E> findEdgeSet(V v1, V v2) {
        return delegate.findEdgeSet(v1, v2);
    }

    @Override
    public V getDest(E directed_edge) {
        return delegate.getDest(directed_edge);
    }

    @Override
    public int getEdgeCount() {
        return delegate.getEdgeCount();
    }

    @Override
    public int getEdgeCount(EdgeType edge_type) {
        return delegate.getEdgeCount(edge_type);
    }

    @Override
    public Collection<E> getEdges() {
        return delegate.getEdges();
    }

    @Override
    public Collection<E> getEdges(EdgeType edgeType) {
        return delegate.getEdges(edgeType);
    }

    @Override
    public EdgeType getEdgeType(E edge) {
        return delegate.getEdgeType(edge);
    }

    @Override
    public EdgeType getDefaultEdgeType() {
        return delegate.getDefaultEdgeType();
    }

    @Override
    public Pair<V> getEndpoints(E edge) {
        return delegate.getEndpoints(edge);
    }

    @Override
    public int getIncidentCount(E edge) {
        return delegate.getIncidentCount(edge);
    }

    @Override
    public Collection<E> getIncidentEdges(V vertex) {
        return delegate.getIncidentEdges(vertex);
    }

    @Override
    public Collection<V> getIncidentVertices(E edge) {
        return delegate.getIncidentVertices(edge);
    }

    @Override
    public Collection<E> getInEdges(V vertex) {
        return delegate.getInEdges(vertex);
    }

    @Override
    public int getNeighborCount(V vertex) {
        return delegate.getNeighborCount(vertex);
    }

    @Override
    public Collection<V> getNeighbors(V vertex) {
        return delegate.getNeighbors(vertex);
    }

    @Override
    public V getOpposite(V vertex, E edge) {
        return delegate.getOpposite(vertex, edge);
    }

    @Override
    public Collection<E> getOutEdges(V vertex) {
        return delegate.getOutEdges(vertex);
    }

    @Override
    public int getPredecessorCount(V vertex) {
        return delegate.getPredecessorCount(vertex);
    }

    @Override
    public Collection<V> getPredecessors(V vertex) {
        return delegate.getPredecessors(vertex);
    }

    @Override
    public V getSource(E directed_edge) {
        return delegate.getSource(directed_edge);
    }

    @Override
    public int getSuccessorCount(V vertex) {
        return delegate.getSuccessorCount(vertex);
    }

    @Override
    public Collection<V> getSuccessors(V vertex) {
        return delegate.getSuccessors(vertex);
    }

    @Override
    public int getVertexCount() {
        return delegate.getVertexCount();
    }

    @Override
    public Collection<V> getVertices() {
        return delegate.getVertices();
    }

    @Override
    public int inDegree(V vertex) {
        return delegate.inDegree(vertex);
    }

    @Override
    public boolean isDest(V vertex, E edge) {
        return delegate.isDest(vertex, edge);
    }

    @Override
    public boolean isPredecessor(V v1, V v2) {
        return delegate.isPredecessor(v1, v2);
    }

    @Override
    public boolean isSource(V vertex, E edge) {
        return delegate.isSource(vertex, edge);
    }

    @Override
    public boolean isSuccessor(V v1, V v2) {
        return delegate.isSuccessor(v1, v2);
    }

    @Override
    public int outDegree(V vertex) {
        return delegate.outDegree(vertex);
    }

    @Override
    public boolean removeEdge(E edge) {
        return delegate.removeEdge(edge);
    }

    @Override
    public boolean removeVertex(V vertex) {
        return delegate.removeVertex(vertex);
    }

    @Override
    public boolean containsEdge(E edge) {
        return delegate.containsEdge(edge);
    }

    @Override
    public boolean containsVertex(V vertex) {
        return delegate.containsVertex(vertex);
    }
}
