package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;

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
        Dimension screenDimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        if (screenDimension == null) screenDimension = new Dimension(1600, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        enableOSXFullscreen(this);
        setVisible(true);
        getContentPane().setPreferredSize(screenDimension);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void enableOSXFullscreen(Window window) {
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void requestToggleFullScreen(Window window)
    {
        try {
            Class appClass = Class.forName("com.apple.eawt.Application");
            Class params[] = new Class[]{};
            Method getApplication = appClass.getMethod("getApplication", params);
            Object application = getApplication.invoke(appClass);
            Method requestToggleFulLScreen = application.getClass().getMethod("requestToggleFullScreen", Window.class);
            requestToggleFulLScreen.invoke(application, window);
        } catch (Exception e) {
            System.out.println("An exception occurred while trying to toggle full screen mode");
        }
    }
}
