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

import java.util.Observable;
import java.util.Observer;

import jsl.observers.ModelElementObserver;


/**
 * Classes extending {@code NetworkGraphObserver} will be informed of changes in
 * the observed {@link NetworkGraph networkGraph}. This class is a specialized
 * version of JSL's {@link ModelElementObserver} for {@code networkGraph}s.
 * Graph changes are relayed through overriding any of four methods:
 * <ul>
 * <li>{@code addedVertex()} - called when a vertex is added to the observed
 * blue.happening.bla.graph,
 * <li>{@code removedVertex()} - called when a vertex is removed from the
 * observed blue.happening.bla.graph,
 * <li>{@code addedEdge()} - called when an edge is added to the observed blue.happening.bla.graph,
 * or
 * <li>{@code removedEdge()} - called when an edge is removed to the observed
 * blue.happening.bla.graph.
 * </ul>
 * With each method, an instance of the added/removed vertex/edge is passed
 * along with the {@code networkGraph} on which the operation occurred. Bear in
 * mind that the same {@code networkGraphObserver} can observe multiple
 * {@code networkGraph}s.
 * <p>
 * Note that {@code NetworkGraph} allows to add vertices and edges that are
 * already members of the given {@code networkGraph}, and similarly remove
 * vertices and edges that are not members of the given {@code networkGraph}. In
 * those instances no actual changes occur to the {@code networkGraph} and as a
 * result the methods in this class are <em>not</em> called. More technically
 * speaking, the methods in this class are only called when the add or remove
 * methods in {@code NetworkGraph} return {@code true}.
 * <p>
 * Warning, extenders should <em>not</em> override the {@code update()} method
 * for the purpose of implementing an observer. This method is used internally
 * to multiplex events to the proper methods.
 * <p>
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 * @see Observer
 */
public abstract class NetworkGraphObserver<V, E> extends ModelElementObserver {

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable observable, Object arg) {

        final NetworkGraph<V, E> graph = (NetworkGraph<V, E>) observable;
        final int state = graph.getObserverState();

        if (state == NetworkGraph.ADDED_VERTEX) {
            final V vertex = graph.getLastAddedVertex();
            addedVertex(graph, vertex);
        } else if (state == NetworkGraph.REMOVED_VERTEX) {
            final V vertex = graph.getLastRemovedVertex();
            removedVertex(graph, vertex);
        } else if (state == NetworkGraph.ADDED_EDGE) {
            final E edge = graph.getLastAddedEdge();
            addedEdge(graph, edge);
        } else if (state == NetworkGraph.REMOVED_EDGE) {
            final E edge = graph.getLastRemovedEdge();
            removedEdge(graph, edge);
        }

        super.update(observable, arg);
    }

    /**
     * This method is called whenever the observed {@code networkGraph} gets a
     * vertex added to it. Note that this method is not always called after one
     * of {@code NetworkGraph}'s {@code addVertex} methods is executed, but only
     * upon successful addition. It does not get called if the vertex addition
     * failed for whatever reason.
     *
     * @param networkGraph the {@code networkGraph} to which {@code vertex} was added
     * @param vertex       the vertex instance that was added to {@code networkGraph}
     */
    protected void addedVertex(final NetworkGraph<V, E> networkGraph,
                               final V vertex) {
    }

    /**
     * This method is called whenever the observed {@code networkGraph} gets a
     * vertex removed from it. Note that this method is not always called after
     * one of {@code NetworkGraph}'s {@code removeVertex} methods is executed,
     * but only upon successful removal. It does not get called if the vertex
     * removal failed for whatever reason.
     *
     * @param networkGraph the {@code networkGraph} to which {@code vertex} was removed
     * @param vertex       the vertex instance that was removed from {@code networkGraph}
     */
    protected void removedVertex(final NetworkGraph<V, E> networkGraph,
                                 final V vertex) {
    }

    /**
     * This method is called whenever the observed {@code networkGraph} gets an
     * edge added to it. Note that this method is not always called after one of
     * {@code NetworkGraph}'s {@code addEdge} methods is executed, but only upon
     * successful addition. It does not get called if the edge addition failed
     * for whatever reason.
     *
     * @param networkGraph the {@code networkGraph} to which {@code vertex} was added
     * @param edge         the edge instance that was added to {@code networkGraph}
     */
    protected void addedEdge(final NetworkGraph<V, E> networkGraph,
                             final E edge) {
    }

    /**
     * This method is called whenever the observed {@code networkGraph} gets an
     * edge removed from it. Note that this method is not always called after
     * one of {@code NetworkGraph}'s {@code removeEdge} methods is executed, but
     * only upon successful removal. It does not get called if the edge removal
     * failed for whatever reason.
     *
     * @param networkGraph the {@code networkGraph} to which {@code vertex} was removed
     * @param edge         the edge instance that was removed from {@code networkGraph}
     */
    protected void removedEdge(final NetworkGraph<V, E> networkGraph,
                               final E edge) {
    }

}
