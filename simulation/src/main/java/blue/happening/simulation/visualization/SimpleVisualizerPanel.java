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

import java.awt.Color;
import java.awt.Dimension;

import blue.happening.simulation.graph.NetworkGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;


public class SimpleVisualizerPanel<V, E> extends VisualizationViewer<V, E> {

    private static final long serialVersionUID = 3201919504663243765L;

    public SimpleVisualizerPanel(NetworkGraph<V, E> graph,
                                 final Dimension preferredSize) {
        super(new DisplacementLayout<V, E>(graph, preferredSize));

        init();
    }

    private void init() {

        // set label render to call toString
        // getRenderContext().setVertexLabelTransformer(new
        // VertexNameLabeller());
        getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());

        // set label render to paint in center of node
        getRenderer().getVertexLabelRenderer()
                .setPosition(Renderer.VertexLabel.Position.CNTR);

        // set label text color
        setForeground(Color.WHITE);

        // add time counter
        // addPostRenderPaintable(new TimeTextPaintable(this, blue.happening.simulation.graph));
        addPostRenderPaintable(new TimeTextPaintable<V, E>(this));

        // add mouse zoom
        final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<String, Number>();
        setGraphMouse(graphMouse);
    }
}
