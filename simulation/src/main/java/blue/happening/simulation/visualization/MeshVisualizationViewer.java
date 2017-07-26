package blue.happening.simulation.visualization;

import java.awt.Color;
import java.awt.Dimension;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.visualization.control.MeshModalGraphMouse;
import blue.happening.simulation.visualization.listener.DeviceMouseListener;
import blue.happening.simulation.visualization.transformer.ConnectionStrokeTransformer;
import blue.happening.simulation.visualization.transformer.DeviceFillPaintTransformer;
import blue.happening.simulation.visualization.transformer.DeviceFontTransformer;
import blue.happening.simulation.visualization.transformer.DeviceLabeler;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;


public class MeshVisualizationViewer extends VisualizationViewer<Device, Connection> {

    private static final long serialVersionUID = 3201919504663243765L;

    public MeshVisualizationViewer(NetworkGraph<Device, Connection> graph, final Dimension preferredSize) {
        super(new DisplacementLayout<>(graph, preferredSize));
        init();
    }

    private void init() {
        // set label render to call toString
        // getRenderContext().setVertexLabelTransformer(new VertexNameLabeller());
        getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Device>());

        // set label render to paint in center of node
        getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        // set label text color
        setForeground(Color.WHITE);

        // add time counter
        addPostRenderPaintable(new TimeTextPaintable<>(this));

        // add mouse zoom
        final AbstractModalGraphMouse graphMouse = new MeshModalGraphMouse<String, Number>();
        setGraphMouse(graphMouse);

        // Custom listener --> add Device click listener
        addGraphMouseListener(new DeviceMouseListener());

        // Custom Colors and Labels
        getRenderContext().setVertexFillPaintTransformer(new DeviceFillPaintTransformer());
        getRenderContext().setEdgeStrokeTransformer(new ConnectionStrokeTransformer());
        getRenderContext().setVertexLabelTransformer(new DeviceLabeler());
        getRenderContext().setVertexFontTransformer(new DeviceFontTransformer());
    }

    public void setGraph(NetworkGraph<Device, Connection> graph) {
        getGraphLayout().setGraph(graph);
    }
}
