package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.internal.VertexProperties;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;


public class MeshVisualizerFrame<V, E> extends JFrame {

    private final NetworkGraph<V, E> graph;
    private final MeshVisualizationViewer<V, E> visualizerPanel;

    public MeshVisualizerFrame(final NetworkGraph<V, E> graph, double repaintHz) {
        super(graph.getName());
        this.graph = graph;
        this.setTitle("Happening Mesh Simulation");

        // add blue.happening.simulation.visualization panel
        final Dimension dimension = new Dimension(getContentPane().getWidth(), getContentPane().getHeight());
        visualizerPanel = new MeshVisualizationViewer<>(graph, dimension);
        getContentPane().add(visualizerPanel);
        new TimedJComponentRepainter(visualizerPanel, (long) (1000 / repaintHz));

        visualizerPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                for (V vertex : graph.getVertices()) {
                    VertexProperties<V, E> properties = graph.getVertexProperties(vertex);
                    RandomDSMobilityPattern pattern = (RandomDSMobilityPattern) properties.getMobilityPattern();
                    RectangularBoundary boundary = pattern.getBoundary();
                    boundary.setWidth(evt.getComponent().getWidth());
                    boundary.setHeight(evt.getComponent().getHeight());
                    pattern.nudge();
                }
            }
        });

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
