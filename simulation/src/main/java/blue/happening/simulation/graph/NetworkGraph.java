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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.internal.AbstractSchedulingElementGraph;
import blue.happening.simulation.graph.internal.Motion;
import blue.happening.simulation.graph.internal.VertexProperties;
import blue.happening.simulation.graph.internal.VerticesDistance;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.StationaryMobilityPattern;
import blue.happening.simulation.visualization.DevicePanel;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Graphs;
import jsl.modeling.Experiment;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.utilities.reporting.JSL;


/**
 * {@code NetworkGraph} represents a MANET and is the main class in a simulation
 * - <b>start here</b>. It extends JSL's {@link ModelElement} and implements
 * JUNG's {@link DirectedGraph}, thus it functions as both a simulation element
 * under JSL, and a fully functional blue.happening.simulation.graph under JUNG. Understanding of JSL and
 * JUNG is not necessary, but highly recommended.
 * <p>
 * The general idea of every simulation is this: you first construct a
 * {@code networkGraph}, populate it with vertices, hand it over to JSL (see
 * below), and run it through a simulation. Throughout the simulation the edges
 * of the {@code networkGraph} get added and removed based on the mobility and
 * communication radii of the vertices. {@link NetworkGraphObserver}s are used
 * to detect these (and other) actions throughout the simulation. This data can
 * then be used for collecting statistics, logging how the MANET behaves, et
 * cetera. Examples of such observers are found in ManetSim's {@code statistic}
 * package.
 * <p>
 * While the simulation is running you can still interact with your
 * {@code networkGraph} by executing the various mutator and inspector methods.
 * In fact, you may even add or remove edges that are not "possible", for
 * example adding an edge between two vertices that are outside each other's
 * communication radii. This will not break the simulation. Of course, you can
 * also interact with your {@code networkGraph} before and after the simulation
 * as well.
 * <p>
 * <b>{@code NetworkGraph} is not synchronized.</b> As a result if another
 * thread, besides the one executing the simulation, access your
 * {@code networkGraph}, you must synchronize those actions externally. This can
 * be achieved by either
 * <ul>
 * <li>pausing the simulation while the other thread is accessing the
 * {@code networkGraph}, or
 * <li>wrapping the target blue.happening.simulation.graph you pass to the {@code NetworkGraph}
 * constructor with {@link Graphs#synchronizedDirectedGraph(DirectedGraph)}.
 * </ul>
 * <p>
 * Executing blue.happening.simulation.graph operations from within JSL events is always safe.
 * <p>
 * {@code NetworkGraph}, just like the rest of ManetSim, uses generics. This
 * allows you the programmer to define the class type of vertices and edges,
 * while still maintaining compile-time type safety. If you are not familiar
 * with generics, start with <a href=
 * "http://download.oracle.com/javase/1.5.0/docs/guide/language/generics.html"
 * >this tutorial</a>.
 * <p>
 * {@code NetworkGraph} is essentially a decorator class that delegates all
 * operations to a target {@code directedGraph}. This target
 * {@code directedGraph} is passed to the constructor of {@code NetworkGraph}.
 * However convenience constructors that do not require a {@code directedGraph}
 * are provided. These constructors instead create a new empty
 * {@code DirectedGraph}.
 * <p>
 * <p>
 * <h3>Edge Pool</h3>
 * Every {@code networkGraph} needs an {@link EdgePool} implementation. The
 * {@code edgePool} is passed into the constructor.
 * <p>
 * As the simulation runs, edges are added to the {@code networkGraph}. Thus an
 * edge pool from which the simulator draws edges is necessary. For example,
 * every time the simulator needs to add a new edge to the {@code networkGraph}
 * from vertex <i>A</i> to vertex <i>B</i>, it asks the {@code edgePool} for
 * that edge object. The {@code edgePool}'s job is then to return this unique
 * edge. Equality is tested using the {@code .equals()} method. Thus the two
 * requirements for edge pools are
 * <ol>
 * <li>the same combination of arguments to
 * {@link EdgePool#getEdge(Object, Object) EdgePool.getEdge(V fromVertex, V
 * toVertex)} must always return equivalent (as defined by {@code .equals()})
 * objects, and
 * <li>every combination of arguments to
 * {@code EdgePool.getEdge(V fromVertex, V toVertex)} must return a different
 * object.
 * </ol>
 * <p>
 * Edge pools must not actually be pools, but can be simple factory methods,
 * just as long as they meet the two requirements above. For example the
 * following is a valid edge pool for a {@code networkGraph} that uses
 * {@code String}s as both vertices and edges:
 * <p>
 * <pre>
 * private static class StringEdgePool implements EdgePool&lt;String, String&gt; {
 *
 * 	&#064;Override
 * 	public String getEdge(String fromVertex, String toVertex) {
 * 		return fromVertex + &quot;=&gt;&quot; + toVertex;
 *    }
 * }
 * </pre>
 * <p>
 * <h3>Constructors</h3>
 * <p>
 * There a total of eight constructors, however you will most likely use the
 * simplest of them most of the time. To construct a {@code NetworkGraph} we
 * need four items:
 * <ol>
 * <li>A parent {@code ModelElement},
 * <li>A name for the new {@code networkGraph},
 * <li>A delegate {@code DirectedGraph}, and
 * <li>An {@code EdgePool} implementation
 * </ol>
 * However only the last, an {@code edgePool} is required. The rest can usually
 * be left up to the library to create. As a result, there are eight
 * constructors providing different combinations of arguments, for whenever
 * necessary. When left to the library, the following are created by default:
 * <ul>
 * <li>for {@code parent} a new JSL {@code Model} object is created,
 * <li>for {@code name} the class name is used, (this is actually a JSL
 * feature), and
 * <li>for {@code networkGraph} a new empty {@code DirectedGraph} is created.
 * </ul>
 * Most times, you will probably want to use the simplest constructor that only
 * requires an {@code edgePool} object.
 * <p>
 * <p>
 * <h3>Java HappeningDemo Library (JSL)</h3>
 * <p>
 * If you are interested in the mechanics of discrete-event simulation, you
 * should check out JSL. This will explain you how a generic discrete-event
 * simulator works. If you are interested in how this package simulates MANET's
 * in a discrete-event fashion, you should first read <a
 * href="http://www.pages.drexel.edu/~sf69/sim_whitepaper.pdf">my whitepaper</a>
 * on the subject, and then explore the code in {@code blue.happening.simulation.graph.internal}.
 * <p>
 * From the prospective of JSL, all you need to know to start simulating MANET's
 * is the following. After you have created your {@code networkGraph}, construct
 * a JSL {@link Experiment experiment} using your {@code networkGraph}'s
 * {@code Model} as the argument, configure the {@code experiment}, and run it.
 * It should look something like this:
 * <p>
 * <pre>
 * &#47&#47 created and configured myNetworkGraph object
 * .
 * .
 * .
 * Experiment experiment = new Experiment(myNetworkGraph.getModel());	&#47&#47 create experiment
 * experiment.setNumberOfReplications(10);				&#47&#47 configure it
 * experiment.setLengthOfReplication(10000);
 * experiment.runAll();							&#47&#47 run it
 * </pre>
 * <p>
 * To keep track of all the simulation elements, JSL builds a tree of objects
 * extending {@code ModelElement}s, with a {@code Model} as it's root. As a
 * result, a parent object must be passed to {@code NetworkGraph} the
 * constructor. However convenience constructors that do not require a parent
 * are provided, see below.
 * <p>
 * JSL web site: <a href=
 * "http://www.uark.edu/~rossetti/research/research_interests/simulation/java_simulation_library_jsl/"
 * target="_simulationnk">http://www.uark.edu/~rossetti/research/research_interests/
 * simulation/java_simulation_library_jsl </a>
 * <p>
 * <h3>Java Universal Network/Graph Framework (JUNG)</h3>
 * <p>
 * From the perspective of JUNG, you can treat your {@code networkGraph} object
 * as any other {@code Graph} object: run algorithms on it, inspect it, mutate
 * it, et cetra. However you must keep in mind that while a simulation is
 * running the {@code networkGraph} is mutating itself and concurrency issues
 * must be taken into account. Generally speaking, if your blue.happening.simulation.graph operation is
 * executed from within a JSL event it is safe. If it is executed from an
 * external thread, (one that isn't executing the simulation), it will probably
 * cause a race condition.
 * <p>
 * JUNG web site: <a href="http://jung.sourceforge.net/"
 * target="_simulationnk">http://jung.sourceforge.net/</a>
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class NetworkGraph<V, E> extends AbstractSchedulingElementGraph<V, E>
        implements DirectedGraph<V, E> {

    // observer constants
    static final int ADDED_VERTEX = JSL.getNextEnumConstant();
    static final int REMOVED_VERTEX = JSL.getNextEnumConstant();
    static final int ADDED_EDGE = JSL.getNextEnumConstant();
    static final int REMOVED_EDGE = JSL.getNextEnumConstant();
    private static Logger logger = LogManager.getLogger(NetworkGraph.class);
    // containers
    private final Map<V, VertexProperties<V, E>> verticesProperties;
    private final EdgePool<V, E> edgePool;

    // notify helper variables
    private V lastAddedVertex = null;
    private V lastRemovedVertex = null;
    private E lastAddedEdge = null;
    private E lastRemovedEdge = null;

    // UI gewurschtel
    private DevicePanel devicePanel = null;
    private Device clickedDevice = null;

    /**
     * Constructs a new {@code NetworkGraph} with {@code parent} ModelElement
     * and {@code edgePool}. A new {@code Model} object will be used as the
     * parent, the class name will be used as the name, and a new empty
     * {@code DirectedGraph} will be as the delegate. This is the simplest
     * {@code NetworkGraph} constructor.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final EdgePool<V, E> edgePool) {
        this(Model.createModel(), edgePool);
    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code name}, and
     * {@code edgePool}. A new {@code Model} object will be used as the parent,
     * and a new empty {@code DirectedGraph} will be as the delegate.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param name     name of the new {@code NetworkGraph} element
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final String name, final EdgePool<V, E> edgePool) {
        this(Model.createModel(), name, edgePool);
    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code delegate}
     * DirectedGraph, and {@code edgePool}. A new {@code Model} object will be
     * used as the parent, and the class name will be used as the name.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param delegate DirectedGraph to delegate operations
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final DirectedGraph<V, E> delegate,
                        final EdgePool<V, E> edgePool) {
        this(Model.createModel(), delegate, edgePool);
    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code name}, {@code delegate}
     * DirectedGraph, and {@code edgePool}. A new {@code Model} object will be
     * used as the parent.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param name     name of the new {@code NetworkGraph} element
     * @param delegate DirectedGraph to delegate operations
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final String name, final DirectedGraph<V, E> delegate,
                        final EdgePool<V, E> edgePool) {
        this(Model.createModel(), name, delegate, edgePool);
    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code parent} ModelElement
     * and {@code edgePool}. The class name will be used as the name, and a new
     * empty {@code DirectedGraph} will be as the delegate.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param parent   the new {@code NetworkGraph} will be a child element of
     *                 {@code parent} in the JSL tree
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final ModelElement parent,
                        final EdgePool<V, E> edgePool) {
        this(parent, new DirectedSparseGraph<V, E>(), edgePool);
    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code parent} ModelElement,
     * {@code name}, and {@code edgePool}. A new empty {@code DirectedGraph}
     * will be as the delegate.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param parent   the new {@code NetworkGraph} will be a child element of
     *                 {@code parent} in the JSL tree
     * @param name     name of the new {@code NetworkGraph} element
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final ModelElement parent, final String name,
                        final EdgePool<V, E> edgePool) {
        this(parent, name, new DirectedSparseGraph<V, E>(), edgePool);
    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code parent} ModelElement,
     * {@code delegate} DirectedGraph, and {@code edgePool}. The class name will
     * be used as the name.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param parent   the new {@code NetworkGraph} will be a child element of
     *                 {@code parent} in the JSL tree
     * @param delegate DirectedGraph to delegate operations
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final ModelElement parent,
                        final DirectedGraph<V, E> delegate, final EdgePool<V, E> edgePool) {
        super(parent, delegate);

        this.verticesProperties = new HashMap<V, VertexProperties<V, E>>();
        this.edgePool = edgePool;
        logger.debug("Graph instantiated.");

    }

    /**
     * Constructs a new {@code NetworkGraph} with {@code parent} ModelElement,
     * {@code name}, {@code delegate} DirectedGraph, and {@code edgePool}. This
     * is the most flexible constructor for {@code NetworkGraph}.
     * <p>
     * See {@code NetworkGraph}'s class documentation for further information
     * regarding construction.
     *
     * @param parent   the new {@code NetworkGraph} will be a child element of
     *                 {@code parent} in the JSL tree
     * @param name     name of the new {@code NetworkGraph} element
     * @param delegate DirectedGraph to delegate operations
     * @param edgePool an edge pool implementation
     */
    public NetworkGraph(final ModelElement parent, final String name,
                        final DirectedGraph<V, E> delegate, final EdgePool<V, E> edgePool) {
        super(parent, name, delegate);

        this.verticesProperties = new HashMap<V, VertexProperties<V, E>>();
        this.edgePool = edgePool;
        logger.debug("Graph instantiated.");
    }

    private static void timeValidationCheck(final double time,
                                            final VertexProperties<?, ?>... properties) {
        assert properties != null;
        double tStart = Double.NEGATIVE_INFINITY;
        double tEnd = Double.POSITIVE_INFINITY;
        for (VertexProperties<?, ?> prop : properties) {
            tStart = Math.max(tStart, prop.getTStart().getValue());
            tEnd = Math.min(tEnd, prop.getTEnd().getValue());
        }
        if (time < tStart || tEnd < time)
            throw new IllegalArgumentException(
                    "invalid time argument: " + time + "; tStart = " + tStart
                            + ", tEnd = " + tEnd);
    }

    /**
     * @see ModelElement#initialize()
     */
    @Override
    protected void initialize() {
        removeAllEdges();
        addStartEdges();
    }

    private void removeAllEdges() {
        List<E> edges = new ArrayList<E>(getEdges());
        for (E e : edges) {
            removeEdge(e);
        }
    }

    private void addStartEdges() {
        for (VertexProperties<V, E> from : verticesProperties.values()) {
            for (VertexProperties<V, E> to : verticesProperties.values()) {
                if (from == to)
                    continue;

                final double txRadius = getRadiusTx(from.getVertex(), to.getVertex());
                final VerticesDistance<V, E> distance = new VerticesDistance<V, E>(this,
                        from.getVertex(), to.getVertex());

                if (distance.getInitialDistance() <= txRadius)
                    addEdge(from.getVertex(), to.getVertex());
            }
        }
    }

    ;

    /**
     * Adds {@code vertex} to this blue.happening.simulation.graph at coordinates (0,0),
     * {@link StationaryMobilityPattern}, and TX and RX radii of
     * {@link Double#MIN_VALUE}.
     *
     * @param vertex the vertex to add
     * @return {@code true} if the add is successful, and {@code false}
     * otherwise
     */
    @Override
    public boolean addVertex(V vertex) {
        final double sx = 0;
        final double sy = 0;
        final MobilityPattern<V, E> mobilityPattern = new StationaryMobilityPattern<V, E>();
        final double txRadius = Double.MIN_VALUE;
        final double rxRadius = Double.MIN_VALUE;
        return addVertex(vertex, sx, sy, mobilityPattern, txRadius, rxRadius);
    }

    ;

    /**
     * Adds {@code vertex} to this blue.happening.simulation.graph at coordinates ({@code sx},{@code sy}),
     * {@code mobilityPattern}, {@code txRadius}, and {@code rxRadius}.
     *
     * @param vertex          the vertex to add
     * @param sx              the initial x-axis coordinate
     * @param sy              the initial y-axis coordinate
     * @param mobilityPattern the mobility pattern
     * @param txRadius        the TX radius
     * @param rxRadius        the RX radius
     * @return {@code true} if the add is successful, and {@code false}
     * otherwise
     */
    public boolean addVertex(V vertex, final double sx, final double sy,
                             final MobilityPattern<V, E> mobilityPattern, final double txRadius,
                             final double rxRadius) {
        final boolean wasAdded = super.addVertex(vertex);
        if (wasAdded) {
            final String name = Integer.toHexString(vertex.hashCode());
            final VertexProperties<V, E> properties = new VertexProperties<V, E>(this,
                    name, vertex, sx, sy, mobilityPattern, txRadius, rxRadius);
            verticesProperties.put(vertex, properties);
            notifyAddedVertexObservers(vertex);

            logger.debug(
                    "Added vertex '" + vertex + "' to blue.happening.simulation.graph: sx=" + sx + ", sy=" + sy
                            + ", mobilityPattern='" + mobilityPattern + "', txRadius="
                            + txRadius + ", rxRadius=" + rxRadius + ".");
        } else {
            logger.warn(
                    "Failed to add vertex '" + vertex + "' to blue.happening.simulation.graph: sx=" + sx + ", sy="
                            + sy + ", mobilityPattern='" + mobilityPattern + "', txRadius="
                            + txRadius + ", rxRadius=" + rxRadius + ".");
        }
        return wasAdded;
    }

    ;

    /**
     * @see DirectedGraph#removeVertex(Object)
     */
    @Override
    public boolean removeVertex(V vertex) {
        final boolean wasRemoved = super.removeVertex(vertex);
        if (wasRemoved) {
            final VertexProperties<V, E> properties = verticesProperties.get(vertex);
            properties.removeFromModel();
            notifyRemovedVertexObservers(vertex);

            logger.debug("Removed vertex '" + vertex + "' from blue.happening.simulation.graph.");
        } else {
            logger.warn("Failed to remove vertex '" + vertex + "' from blue.happening.simulation.graph.");
        }
        return wasRemoved;
    }

    /**
     * Adds {@code edge} incident from {@code fromVertex} and incident to
     * {@code toVertex}.
     *
     * @param fromVertex the vertex {@code edge} is incident from
     * @param toVertex   the vertex {@code edge} is incident to
     * @return {@code true} if the add is successful, and {@code false}
     * otherwise
     */
    public boolean addEdge(V fromVertex, V toVertex) {

        if (fromVertex.equals(toVertex))
            return false;

        final VertexProperties<V, E> fromProperties = verticesProperties
                .get(fromVertex);
        final VertexProperties<V, E> toProperties = verticesProperties
                .get(fromVertex);

        if (fromProperties == null || toProperties == null)
            return false;

        final E edge = edgePool.getEdge(fromVertex, toVertex);
        final boolean wasAdded = addEdge(edge, fromVertex, toVertex);
        if (wasAdded) {
            notifyAddedEdgeObservers(edge);
            logger.debug(
                    "Added edge '" + edge + "' from '" + fromVertex + "' to '" + toVertex
                            + "'.");
        } else {
            logger.warn(
                    "Failed to add edge '" + edge + "' from '" + fromVertex + "' to '"
                            + toVertex + "'.");
        }
        return wasAdded;
    }

    /**
     * Removes the edge incident from {@code fromVertex} and incident to
     * {@code toVertex}.
     *
     * @param fromVertex the vertex that the edge is incident from
     * @param toVertex   the vertex that the edge is incident to
     * @return {@code true} if the remove is successful, and {@code false}
     * otherwise
     */
    public boolean removeEdge(V fromVertex, V toVertex) {

        final VertexProperties<V, E> fromProperties = verticesProperties
                .get(fromVertex);
        final VertexProperties<V, E> toProperties = verticesProperties
                .get(fromVertex);

        if (fromProperties == null || toProperties == null)
            return false;

        final E edge = edgePool.getEdge(fromVertex, toVertex);
        final boolean wasRemoved = removeEdge(edge);
        if (wasRemoved) {
            notifyRemovedEdgeObservers(edge);
            logger.debug("Removed edge '" + edge + "' from '" + fromVertex + "' to '"
                    + toVertex + "'.");
        } else {
            logger.warn(
                    "Failed to remove edge '" + edge + "' from '" + fromVertex + "' to '"
                            + toVertex + "'.");
        }
        return wasRemoved;
    }

    public boolean removeEdges(V toVertex) {
        boolean allRemoved = true;
        for (E edge : getIncidentEdges(toVertex)) {
            final boolean wasRemoved = removeEdge(edge);
            if (wasRemoved) {
                notifyRemovedEdgeObservers(edge);
                logger.debug("Removed edge '" + edge + "' to '" + toVertex + "'.");
            } else {
                allRemoved = false;
                logger.warn("Failed to remove edge '" + edge + "' to '" + toVertex + "'.");
            }
        }
        return allRemoved;
    }

    public boolean addEdges(V fromVertex) {
        boolean allAdded = true;
        for (VertexProperties<V, E> to : verticesProperties.values()) {
            if (fromVertex == to.getVertex()) {
                continue;
            }
            final VerticesDistance<V, E> distance = new VerticesDistance<>(this,
                    fromVertex, to.getVertex());
            final double fromTxRadius = getRadiusTx(fromVertex, to.getVertex());
            final double toTxRadius = getRadiusTx(to.getVertex(), fromVertex);
            if (distance.getDistance() <= fromTxRadius) {
                allAdded &= addEdge(fromVertex, to.getVertex());
            }
            if (distance.getDistance() <= toTxRadius) {
                allAdded &= addEdge(to.getVertex(), fromVertex);
            }
        }
        return allAdded;
    }

    /**
     * Return {@code true} if an edge incident from {@code fromVertex} and
     * incident to {@code toVertex} exists in blue.happening.simulation.graph, {@code false } otherwise.
     *
     * @param fromVertex the vertex that the edge is incident from
     * @param toVertex   the vertex that the edge is incident to
     * @return {@code true} if an edge incident from {@code fromVertex} and
     * incident to {@code toVertex} exists, {@code false} otherwise
     */
    public boolean containsEdge(V fromVertex, V toVertex) {
        E edge = edgePool.getEdge(fromVertex, toVertex);
        return containsEdge(edge);
    }

    public VertexProperties<V, E> getVertexProperties(final V vertex) {
        return verticesProperties.get(vertex);
    }

    /**
     * Returns the initial x-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the initial x-axis coordinate of {@code vertex}
     */
    public double getInitialDisplacementX(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double sxi = properties.getSx().getInitialValue();
        return sxi;
    }

    /**
     * Returns the initial y-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the initial y-axis coordinate of {@code vertex}
     */
    public double getInitialDisplacementY(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double syi = properties.getSy().getInitialValue();
        return syi;
    }

    /**
     * Returns the start x-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the start x-axis coordinate of {@code vertex}
     */
    public double getStartDisplacementX(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double sxi = properties.getSx().getValue();
        return sxi;
    }

    /**
     * Returns the start y-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the start y-axis coordinate of {@code vertex}
     */
    public double getStartDisplacementY(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double syi = properties.getSy().getValue();
        return syi;
    }

    /**
     * Returns the end x-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the end x-axis coordinate of {@code vertex}
     */
    public double getEndDisplacementX(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double time = properties.getTEnd().getValue();
        return getDisplacementX(vertex, time);
    }

    /**
     * Returns the end y-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the end y-axis coordinate of {@code vertex}
     */
    public double getEndDisplacementY(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double time = properties.getTEnd().getValue();
        return getDisplacementY(vertex, time);
    }

    /**
     * Returns the previous start x-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the previous start x-axis coordinate of {@code vertex}
     */
    public double getPreviousStartDisplacementX(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double sxp = properties.getSx().getPreviousValue();
        return sxp;
    }

    /**
     * Returns the previous start y-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the previous start y-axis coordinate of {@code vertex}
     */
    public double getPreviousStartDisplacementY(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double syp = properties.getSy().getPreviousValue();
        return syp;
    }

    /**
     * Returns the current x-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the current x-axis coordinate of {@code vertex}
     */
    public double getDisplacementX(final V vertex) {
        final double time = getTime();
        return getDisplacementX(vertex, time);
    }

    /**
     * Returns the x-axis coordinate of {@code vertex} at {@code time}.
     * {@code time} must be a future time.
     *
     * @param vertex the vertex to query
     * @return the x-axis coordinate of {@code vertex} at {@code time}
     * @throws IllegalArgumentException if {@code time} is not a future time
     */
    public double getDisplacementX(final V vertex, final double time)
            throws IllegalArgumentException {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        timeValidationCheck(time, properties);

        final double s = properties.getSx().getValue();
        final double v = properties.getVx().getValue();
        final double tStart = properties.getTStart().getValue();
        final double t = time - tStart;
        return Motion.solveForFinalDisplacement(s, v, t);
    }

    /**
     * Returns the current y-axis coordinate of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the current y-axis coordinate of {@code vertex}
     */
    public double getDisplacementY(final V vertex) {
        final double time = getTime();
        return getDisplacementY(vertex, time);
    }

    /**
     * Returns the y-axis coordinate of {@code vertex} at {@code time}.
     * {@code time} must be a future time.
     *
     * @param vertex the vertex to query
     * @return the y-axis coordinate of {@code vertex} at {@code time}
     * @throws IllegalArgumentException if {@code time} is not a future time
     */
    public double getDisplacementY(final V vertex, final double time)
            throws IllegalArgumentException {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        timeValidationCheck(time, properties);

        final double s = properties.getSy().getValue();
        final double v = properties.getVy().getValue();
        final double tStart = properties.getTStart().getValue();
        final double t = time - tStart;
        return Motion.solveForFinalDisplacement(s, v, t);
    }

    /**
     * Returns the x-axis velocity of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the x-axis velocity of {@code vertex}
     */
    public double getVelocityX(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double vx = properties.getVx().getValue();
        return vx;
    }

    /**
     * Returns the y-axis velocity of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the y-axis velocity of {@code vertex}
     */
    public double getVelocityY(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double vy = properties.getVy().getValue();
        return vy;
    }

    /**
     * Returns the previous x-axis velocity of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the previous x-axis velocity of {@code vertex}
     */
    public double getPreviousVelocityX(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double vxp = properties.getVx().getPreviousValue();
        return vxp;
    }

    /**
     * Returns the previous y-axis velocity of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the previous y-axis velocity of {@code vertex}
     */
    public double getPreviousVelocityY(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double vyp = properties.getVy().getPreviousValue();
        return vyp;
    }

    /**
     * Returns the current start time of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the current start time of {@code vertex}
     */
    public double getTimeStart(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        return properties.getTStart().getValue();
    }

    /**
     * Returns the current end time of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the current end time of {@code vertex}
     */
    public double getTimeEnd(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        return properties.getTEnd().getValue();
    }

    /**
     * Returns the previous start time of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the previous start time of {@code vertex}
     */
    public double getPreviousTimeStart(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double tStartPrev = properties.getTStart().getPreviousValue();
        return tStartPrev;
    }

    /**
     * Returns the previous end time of {@code vertex}.
     *
     * @param vertex the vertex to query
     * @return the previous end time of {@code vertex}
     */
    public double getPreviousTimeEnd(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double tEndPrev = properties.getTEnd().getPreviousValue();
        return tEndPrev;
    }

    /**
     * Returns {@code vertex}'s TX radius.
     *
     * @param vertex the vertex to query
     * @return {@code vertex}'s TX radius.
     */
    public double getRadiusTx(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double radiusTx = properties.getTxRadius();
        return radiusTx;
    }

    /**
     * Returns {@code vertex}'s RX radius.
     *
     * @param vertex the vertex to query
     * @return {@code vertex}'s RX radius.
     */
    public double getRadiusRx(final V vertex) {
        final VertexProperties<V, E> properties = verticesProperties.get(vertex);
        final double radiusRx = properties.getRxRadius();
        return radiusRx;
    }

    /**
     * Returns the maximal radius for TX from {@code fromVertex} to
     * {@code toVertex}.
     *
     * @param fromVertex transmitting vertex
     * @param toVertex   receiving vertex
     * @return the maximal radius for TX from {@code fromVertex} to
     * {@code toVertex}
     */
    public double getRadiusTx(final V fromVertex, final V toVertex) {
        final VertexProperties<V, E> fromProperties = verticesProperties
                .get(fromVertex);
        final VertexProperties<V, E> toProperties = verticesProperties
                .get(toVertex);
        final double fromTx = fromProperties.getTxRadius();
        final double toRx = toProperties.getRxRadius();
        return Math.max(fromTx, toRx);
    }

    /**
     * Returns the maximal radius for TX from {@code toVertex} to
     * {@code fromVertex}.
     *
     * @param fromVertex receiving vertex
     * @param toVertex   transmitting vertex
     * @return the maximal radius for TX from {@code toVertex} to
     * {@code fromVertex}
     */
    public double getRadiusRx(final V fromVertex, final V toVertex) {
        final VertexProperties<V, E> fromProperties = verticesProperties
                .get(fromVertex);
        final VertexProperties<V, E> toProperties = verticesProperties
                .get(toVertex);
        final double fromRx = fromProperties.getRxRadius();
        final double toTx = toProperties.getTxRadius();
        return Math.max(fromRx, toTx);
    }

    /**
     * This method is used to notify observers that {@code vertex} has been
     * added to this blue.happening.simulation.graph.
     *
     * @param vertex the vertex that was added
     */
    protected final void notifyAddedVertexObservers(final V vertex) {
        lastAddedVertex = vertex;
        notifyObservers(ADDED_VERTEX);
    }

    /**
     * This method is used to notify observers that {@code vertex} has been
     * removed from this blue.happening.simulation.graph.
     *
     * @param vertex the vertex that was removed
     */
    protected final void notifyRemovedVertexObservers(final V vertex) {
        lastRemovedVertex = vertex;
        notifyObservers(REMOVED_VERTEX);
    }

    /**
     * This method is used to notify observers that {@code edge} has been added
     * to this blue.happening.simulation.graph.
     *
     * @param edge the edge that was added
     */
    protected final void notifyAddedEdgeObservers(final E edge) {
        lastAddedEdge = edge;
        notifyObservers(ADDED_EDGE);
    }

    /**
     * This method is used to notify observers that {@code edge} has been
     * removed to this blue.happening.simulation.graph.
     *
     * @param edge the edge that was removed
     */
    protected final void notifyRemovedEdgeObservers(final E edge) {
        lastRemovedEdge = edge;
        notifyObservers(REMOVED_EDGE);
    }

    /**
     * Returns the {@code vertex} that was last added to this blue.happening.simulation.graph. If no
     * vertex has yet been added, {@code null} is returned.
     *
     * @return the {@code vertex} that was last added to this blue.happening.simulation.graph. If no
     * vertex has yet been added, {@code null} is returned
     */
    public V getLastAddedVertex() {
        return lastAddedVertex;
    }

    /**
     * Returns the {@code vertex} that was last removed from this blue.happening.simulation.graph. If no
     * vertex has yet been removed, {@code null} is returned.
     *
     * @return the {@code vertex} that was last removed from this blue.happening.simulation.graph. If no
     * vertex has yet been removed, {@code null} is returned
     */
    public V getLastRemovedVertex() {
        return lastRemovedVertex;
    }

    /**
     * Returns the {@code edge} that was last added to this blue.happening.simulation.graph. If no edge
     * has yet been added, {@code null} is returned.
     *
     * @return the {@code edge} that was last added to this blue.happening.simulation.graph. If no edge
     * has yet been added, {@code null} is returned
     */
    public E getLastAddedEdge() {
        return lastAddedEdge;
    }

    /**
     * Returns the {@code edge} that was last removed from this blue.happening.simulation.graph. If no
     * edge has yet been removed, {@code null} is returned.
     *
     * @return the {@code edge} that was last removed from this blue.happening.simulation.graph. If no
     * edge has yet been removed, {@code null} is returned
     */
    public E getLastRemovedEdge() {
        return lastRemovedEdge;
    }

    public DevicePanel getDevicePanel() {
        return devicePanel;
    }

    public void setDevicePanel(DevicePanel devicePanel) {
        this.devicePanel = devicePanel;
    }

    public Device getClickedDevice() {
        return clickedDevice;
    }

    public void setClickedDevice(Device clickedDevice) {
        this.clickedDevice = clickedDevice;
    }
}
