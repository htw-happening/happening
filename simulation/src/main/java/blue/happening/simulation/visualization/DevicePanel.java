package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.RemoteDevice;
import blue.happening.simulation.entities.Device;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;
    private JTable table;
    private JLabel deviceLabel;
    private JButton btn_sendMessage;
    private JSlider package_drop_slider;
    private JSlider package_delay_slider;
    private Device device;
    private List<RemoteDevice> selectedDevices;

    public DevicePanel() {
        selectedDevices = new ArrayList<>();
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        deviceLabel = new JLabel("Current Device", JLabel.LEFT);
        JButton btn_disable = new JButton("Disable Device");
        btn_sendMessage = new JButton("Send message");
        btn_sendMessage.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(deviceLabel);
        btnPanel.add(btn_disable);
        btnPanel.add((btn_sendMessage));
        add(btnPanel);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        package_drop_slider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 0);
        package_drop_slider.setMajorTickSpacing(10);
        package_drop_slider.setMinorTickSpacing(10);
        package_drop_slider.setPaintTicks(true);
        package_drop_slider.setPaintLabels(true);
        sliderPanel.add(new JLabel("Package Drop Rate", JLabel.CENTER));
        sliderPanel.add(package_drop_slider);

        package_delay_slider = new JSlider(JSlider.HORIZONTAL,
                0, 1000, 0);
        package_delay_slider.setMajorTickSpacing(100);
        package_delay_slider.setMinorTickSpacing(100);
        package_delay_slider.setPaintTicks(true);
        package_delay_slider.setPaintLabels(true);
        sliderPanel.add(new JLabel("Package Send Delay", JLabel.CENTER));
        sliderPanel.add(package_delay_slider);

        add(sliderPanel);

        table = new JTable();
        table.setAutoCreateRowSorter(true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane);

        setVisible(true);

        package_drop_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                device.getMockLayer().setMessageLoss((float) source.getValue() / 100);
            }
        });

        package_delay_slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                device.setMessageDelay(source.getValue());
            }
        });

        btn_sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (RemoteDevice remotedevice : selectedDevices) {
                    device.sendMessageTo(remotedevice.getUuid(), "Hello".getBytes());
                }
            }
        });
    }

    public void updateMessageLossSlider(Device device) {
        int newVal = (int) (device.getMockLayer().getMessageLoss() * 100);
        if (newVal != package_drop_slider.getValue()) {
            package_drop_slider.setValue(newVal);
            package_drop_slider.updateUI();
        }
    }

    public void updatePackageDelay(Device device) {
        int newVal = (int) (device.getMessageDelay());
        if (newVal != package_delay_slider.getValue()) {
            package_delay_slider.setValue(newVal);
            package_delay_slider.updateUI();
        }
    }

    public void setNeighbourList(Device device) {
        List<MeshDevice> neighbours = device.getDevices();
        DeviceNeighbourTableModel neighbourTableModel = new DeviceNeighbourTableModel(neighbours);
        table.setModel(neighbourTableModel);
        table.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
        table.updateUI();
    }

    public void setDevice(Device device) {
        this.device = device;
        updateMessageLossSlider(device);
        updatePackageDelay(device);
        setNeighbourList(device);
        deviceLabel.setText(device.getName());
    }

    public void updateDevice(Device device) {
        setNeighbourList(device);
        updateMessageLossSlider(device);
        updatePackageDelay(device);
    }

    private void setSelectedDevicesFromSelectedDeviceNames(List<String> selectedDeviceNames) {
        selectedDevices.clear();
        for (String name : selectedDeviceNames) {
            RemoteDevice selectedDevice = device.getMeshHandler().getRoutingTable().get(name);
            selectedDevices.add(selectedDevice);
            device.getMeshHandler().sendMessage("ABC".getBytes(), selectedDevice.getUuid());
        }
        if (selectedDevices.size() > 0) {
            btn_sendMessage.setEnabled(true);
        } else {
            btn_sendMessage.setEnabled(false);
        }
    }

    private class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            List<String> selectedDevicesNames = new ArrayList<>();
            if (!e.getValueIsAdjusting()) {
                for (int selectedRow : table.getSelectedRows()) {
                    selectedDevicesNames.add((String) table.getValueAt(selectedRow, 0));
                }
                setSelectedDevicesFromSelectedDeviceNames(selectedDevicesNames);
            }
        }
    }
}
