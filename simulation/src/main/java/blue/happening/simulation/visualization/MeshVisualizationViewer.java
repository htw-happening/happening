package blue.happening.simulation.visualization;

import java.awt.Color;
import java.awt.Dimension;

import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.visualization.listener.DeviceMouseListener;
import blue.happening.simulation.visualization.transformer.ConnectionLabeller;
import blue.happening.simulation.visualization.transformer.ConnectionStrokeTransformer;
import blue.happening.simulation.visualization.transformer.DeviceFillPaintTransformer;
import blue.happening.simulation.visualization.transformer.DeviceLabeller;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;


public class MeshVisualizationViewer<Device, Connection>
        extends VisualizationViewer<Device, Connection> {

    // private static final long serialVersionUID = 3201919504663243765L;

    public MeshVisualizationViewer(NetworkGraph<Device, Connection> graph,
                                   final Dimension preferredSize) {
        super(new DisplacementLayout<Device, Connection>(graph, preferredSize));

        init();
    }

    private void init() {

        // set label render to call toString
        // getRenderContext().setVertexLabelTransformer(new
        // VertexNameLabeller());
        getRenderContext()
                .setVertexLabelTransformer(new ToStringLabeller<Device>());

        // set label render to paint in center of node
        getRenderer().getVertexLabelRenderer()
                .setPosition(Renderer.VertexLabel.Position.CNTR);

        // set label text color
        setForeground(Color.WHITE);

        // add time counter
        // addPostRenderPaintable(new TimeTextPaintable(this, blue.happening.bla.graph));
        addPostRenderPaintable(new TimeTextPaintable<Device, Connection>(this));

        // add mouse zoom
        final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<String, Number>();
        setGraphMouse(graphMouse);

        // Custom listener --> add Device click listener
        addGraphMouseListener(new DeviceMouseListener());

        // Custom Color --> Event handling for blue.happening.bla.visualization
        getRenderContext()
                .setVertexFillPaintTransformer(new DeviceFillPaintTransformer());
        getRenderContext()
                .setEdgeStrokeTransformer(new ConnectionStrokeTransformer());
        // Custom Labels
        getRenderContext().setVertexLabelTransformer(new DeviceLabeller());
        getRenderContext().setEdgeLabelTransformer(new ConnectionLabeller());

    }

}