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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import blue.happening.simulation.graph.NetworkGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;


public class SimpleVisualizerFrame<V, E> extends JFrame {

    private static final long serialVersionUID = -3475681140113319829L;

    private final NetworkGraph<V, E> graph;
    private final SimpleVisualizerPanel<V, E> visualizerPanel;

    public SimpleVisualizerFrame(NetworkGraph<V, E> graph) {
        super("Graph " + graph.getName());
        this.graph = graph;

        // add blue.happening.simulation.visualization panel
        Dimension preferredSize = new Dimension(10000, 10000);
        this.visualizerPanel = new SimpleVisualizerPanel<V, E>(graph,
                preferredSize);
        getContentPane().add(new GraphZoomScrollPane(visualizerPanel));
        // new JComponentRepaintAction(blue.happening.simulation.graph, "test", visualizerPanel, 0.01);
        new TimedJComponenetRepainter(visualizerPanel, 15);

        // add buttons panel
        ButtonsPanel<V, E> buttonsPanel = new ButtonsPanel<V, E>(visualizerPanel);
        add(buttonsPanel, BorderLayout.SOUTH);

        // pack and view
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public NetworkGraph<V, E> getGraph() {
        return graph;
    }

    public SimpleVisualizerPanel<V, E> getVisualizerPanel() {
        return visualizerPanel;
    }
}
