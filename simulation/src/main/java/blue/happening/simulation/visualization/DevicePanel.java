package blue.happening.simulation.visualization;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;

    public DevicePanel() {
        this.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setLayout(new FlowLayout());

        JLabel lab1 = new JLabel("Current Device", JLabel.LEFT);
        this.add(lab1);

        JButton btn_disable = new JButton("Disable Device");
        this.add(btn_disable);
    }
}
