package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.internal.VertexProperties;
import blue.happening.simulation.mobility.MobilityPattern;


public class MeshVisualizerFrame extends JFrame {

    private DevicePanel devicePanel;
    private TimedJComponentRepainter repainter;
    private MeshVisualizationViewer visualizerPanel;

    public MeshVisualizerFrame() {
        super("Happening Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        getContentPane().setPreferredSize(new Dimension(1600, 1000));
        pack();
    }

    public void init() {
        Dimension dimension = new Dimension(getContentPane().getWidth() - 500, getContentPane().getHeight());
        visualizerPanel = new MeshVisualizationViewer(HappeningDemo.getGraph(), dimension);
        getContentPane().add(visualizerPanel);
        repainter = new TimedJComponentRepainter(visualizerPanel, 15);

        visualizerPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                for (Device device : HappeningDemo.getGraph().getVertices()) {
                    VertexProperties<Device, Connection> properties = HappeningDemo.getGraph().getVertexProperties(device);
                    MobilityPattern pattern = properties.getMobilityPattern();
                    pattern.nudge(evt.getComponent().getWidth(), evt.getComponent().getHeight());
                }
            }
        });

        devicePanel = new DevicePanel();
        devicePanel.setPreferredSize(new Dimension(500, 1000));
        add(devicePanel, BorderLayout.EAST);

        pack();
    }

    public void destroy() {
        repainter.cancel();
        remove(devicePanel);
        getContentPane().remove(visualizerPanel);
    }

    public DevicePanel getDevicePanel() {
        return devicePanel;
    }
}
