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

import blue.happening.simulation.graph.EdgePool;
import blue.happening.simulation.graph.NetworkGraph;
import jsl.modeling.ModelElement;


/**
 * This class is an example of how to create your own {@link NetworkGraph} and
 * {@link EdgePool}. In fact, usually you won't need to extend
 * {@code NetworkGraph} but just construct it directly with generics, such as:
 * <p>
 * <pre>
 * &#47&#47 construct your edge pool object
 * .
 * .
 * .
 * NetworkGraph&lt;String, String&gt; myGraph = new NetworkGraph&lt;String, String&gt;(myEdgePool);
 * </pre>
 * <p>
 * This class is used in many of the demos.
 *
 * @author Semyon Fishman (sf69@drexel.edu)
 */
public class StringStringNetworkGraph extends NetworkGraph<String, String> {

    public StringStringNetworkGraph() {
        super(new StringEdgePool());
    }

    public StringStringNetworkGraph(ModelElement parent) {
        super(parent, new StringEdgePool());
    }

    private static class StringEdgePool implements EdgePool<String, String> {

        @Override
        public String getEdge(String fromVertex, String toVertex) {
            return fromVertex + "=>" + toVertex;
        }

    }
}
