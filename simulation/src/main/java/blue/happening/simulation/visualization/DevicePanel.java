package blue.happening.simulation.visualization;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import blue.happening.mesh.RemoteDevice;
import blue.happening.simulation.entities.Device;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;
    private JLabel deviceInfo;
    private Device device;

    public DevicePanel() {
        this.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setLayout(new FlowLayout());

        JLabel lab1 = new JLabel("Current Device", JLabel.LEFT);
        this.add(lab1);

        JButton btn_disable = new JButton("Disable Device");
        this.add(btn_disable);

        JButton btn_sendMessage = new JButton("Send message");
        this.add(btn_sendMessage);

        deviceInfo = new JLabel("No Device Selected", JLabel.LEFT);
        this.add(deviceInfo);

        btn_sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(String uuid:device.getMeshHandler().getDevices()){
                    device.getMeshHandler().sendMessage(uuid, "Hello".getBytes());
                }
            }
        });
    }

    public void setDevice(Device device) {
        this.device = device;
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(device.getName());
        for (RemoteDevice remoteDevice : device.getMeshHandler().getRoutingTable().values()) {
            if (remoteDevice.isNeighbour()) {
                builder.append(remoteDevice.getUuid());
                builder.append(": ");
                builder.append(String.format("%.2f",remoteDevice.getEq()));
                builder.append("/");
                builder.append(String.format("%.2f",remoteDevice.getRq()));
                builder.append("=");
                builder.append(String.format("%.2f",remoteDevice.getTq()));
                builder.append(", ");
            }
        }
        deviceInfo.setText(builder.toString());
    }
}
