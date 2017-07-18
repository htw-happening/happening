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

package blue.happening.simulation.visualization;

import org.apache.commons.collections15.Transformer;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import blue.happening.simulation.graph.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;


public class DisplacementLayout<V, E> extends AbstractLayout<V, E> {

    private final DisplacementTransformer<V, E> transformer;

    public DisplacementLayout(NetworkGraph<V, E> graph) {
        super(graph, new DisplacementTransformer<V, E>(graph));
        transformer = new DisplacementTransformer<V, E>(graph);
    }

    public DisplacementLayout(NetworkGraph<V, E> graph, Dimension size) {
        super(graph, new DisplacementLayout<V, E>(graph), size);
        transformer = new DisplacementTransformer<V, E>(graph);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void reset() {
    }

    public Point2D transform(V v) {
        return transformer.transform(v);
    }

    private static class DisplacementTransformer<V, E>
            implements Transformer<V, Point2D> {

        private final NetworkGraph<V, E> graph;

        public DisplacementTransformer(final NetworkGraph<V, E> graph) {
            this.graph = graph;
        }

        @Override
        public Point2D transform(V vertex) {
            double x = graph.getDisplacementX(vertex);
            double y = graph.getDisplacementY(vertex);
            return new Point2D.Double(x, y);
        }
    }
}
