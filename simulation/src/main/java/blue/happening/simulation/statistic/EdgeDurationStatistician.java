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

package blue.happening.simulation.statistic;

import java.util.HashMap;
import java.util.Map;

import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.NetworkGraphObserver;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.statistic.StatisticAccessorIfc;


/**
 * An {@code edgeDurationStatistician} is used to collect statistical
 * information about the <em>edge durations</em> in a {@link NetworkGraph
 * networkGraph} as it is mutating throughout a simulation.
 * <p>
 * To use this class, construct an instance of it using your
 * {@code networkGraph} as the argument, <em>before</em> the start of the
 * simulation. After the simulation, use the
 * {@code getAcrossReplicationStatistic()} method to get a
 * {@link StatisticAccessorIfc}.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class EdgeDurationStatistician<V, E> {

    private final Map<E, Double> startTimes = new HashMap<E, Double>();

    private final ResponseVariable duration;

    public EdgeDurationStatistician(final NetworkGraph<V, E> graph) {
        graph.addObserver(new EdgeDurationMeterObserver());
        duration = new ResponseVariable(graph);
    }

    /**
     * Returns a {@code statisticAccessorIfc} for the across replication
     * statistics for the edge duration.
     * <p>
     * Call this <em>after</em> the simulation is over.
     *
     * @return a {@code statisticAccessorIfc} for the across replication
     * statistics that have been collected
     */
    public StatisticAccessorIfc getAcrossReplicationStatistic() {
        return duration.getAcrossReplicationStatistic();
    }

    /**
     * Returns the average edge duration. For other statistics use
     * {@code getAcrossReplicationStatistic()}.
     * <p>
     * Call this <em>after</em> the simulation is over.
     *
     * @return the average edge duration
     */
    public double getAcrossReplicationAverageEdgeDuration() {
        return duration.getAcrossReplicationAverage();
    }

    private class EdgeDurationMeterObserver extends NetworkGraphObserver<V, E> {

        @Override
        protected void addedEdge(final NetworkGraph<V, E> networkGraph,
                                 final E edge) {

            final double startTime = ModelElement.getTime();
            startTimes.put(edge, startTime);
        }

        ;

        @Override
        protected void removedEdge(final NetworkGraph<V, E> networkGraph,
                                   final E edge) {

            final double startTime = startTimes.remove(edge);
            final double endTime = ModelElement.getTime();

            duration.setValue(endTime - startTime);
        }

        ;

    }

}
