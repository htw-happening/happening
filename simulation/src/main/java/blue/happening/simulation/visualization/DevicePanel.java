package blue.happening.simulation.visualization;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import blue.happening.mesh.RemoteDevice;
import blue.happening.simulation.entities.Device;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;
    private JLabel deviceInfo;

    public DevicePanel() {
        this.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setLayout(new FlowLayout());

        JLabel lab1 = new JLabel("Current Device", JLabel.LEFT);
        this.add(lab1);

        JButton btn_disable = new JButton("Disable Device");
        this.add(btn_disable);

        deviceInfo = new JLabel("No Device Selected", JLabel.LEFT);
        this.add(deviceInfo);
    }

    public void setDevice(Device device) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(device.getName());
        for (RemoteDevice remoteDevice : device.getMeshHandler().getRoutingTable().values()) {
            if (remoteDevice.isNeighbour()) {
                builder.append(remoteDevice.getUuid());
                builder.append(": ");
                builder.append(remoteDevice.getEq());
                builder.append("/");
                builder.append(remoteDevice.getRq());
                builder.append("=");
                builder.append(remoteDevice.getTq());
                builder.append(", ");
            }
        }
        deviceInfo.setText(builder.toString());
    }
}
