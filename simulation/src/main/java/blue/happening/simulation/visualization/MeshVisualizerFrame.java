package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import blue.happening.simulation.graph.NetworkGraph;


public class MeshVisualizerFrame<V, E> extends JFrame {

    private final NetworkGraph<V, E> graph;
    private final MeshVisualizationViewer<V, E> visualizerPanel;

    public MeshVisualizerFrame(NetworkGraph<V, E> graph) {
        super(graph.getName());
        this.graph = graph;

        // add blue.happening.simulation.visualization panel
        Dimension dimension = new Dimension(getContentPane().getWidth(), getContentPane().getHeight());
        this.visualizerPanel = new MeshVisualizationViewer<>(graph, dimension);
        getContentPane().add(visualizerPanel);
        new TimedJComponentRepainter(visualizerPanel, 15);

        // add device panel
        DevicePanel panel = new DevicePanel();
        add(panel, BorderLayout.EAST);
        graph.setDevicePanel(panel);

        // pack and view
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public NetworkGraph<V, E> getGraph() {
        return graph;
    }

    public MeshVisualizationViewer<V, E> getVisualizerPanel() {
        return visualizerPanel;
    }

}
