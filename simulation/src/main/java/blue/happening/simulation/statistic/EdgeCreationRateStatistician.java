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

import java.util.ArrayList;
import java.util.List;

import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.NetworkGraphObserver;
import jsl.modeling.ModelElement;


/**
 * An {@code edgeCreationRateStatistician} is used to collect statistical
 * information about the <em>creation rate of edges</em> in a
 * {@link NetworkGraph networkGraph} as it is mutating throughout a simulation.
 * <p>
 * To use this class, construct an instance of it using your
 * {@code networkGraph} as the argument, <em>before</em> the start of the
 * simulation. After the simulation, use the
 * {@code getAcrossReplicationAverageRate()} method to get the average.
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class EdgeCreationRateStatistician<V, E> {

    private final List<Double> samlpes = new ArrayList<Double>();
    private double counter;

    public EdgeCreationRateStatistician(final NetworkGraph<V, E> graph) {
        graph.addObserver(new EdgeDurationMeterObserver());
    }

    /**
     * Returns the average edge creation rate.
     * <p>
     * Call this <em>after</em> the simulation is over.
     *
     * @return the average edge creation rate.
     */
    public double getAcrossReplicationAverageRate() {
        double sum = 0;
        for (double d : samlpes) {
            sum += d;
        }
        return (sum / samlpes.size());
    }

    private class EdgeDurationMeterObserver extends NetworkGraphObserver<V, E> {

        @Override
        protected void beforeExperiment(ModelElement m, Object arg) {
            samlpes.clear();
        }

        @Override
        protected void beforeReplication(ModelElement m, Object arg) {
            counter = 0;
        }

        @Override
        protected void addedEdge(final NetworkGraph<V, E> networkGraph,
                                 final E edge) {
            counter++;
        }

        ;

        @Override
        protected void afterReplication(ModelElement m, Object arg) {
            final double time = m.getCurrentReplication().getTime();
            samlpes.add(counter / time);
        }

    }

}