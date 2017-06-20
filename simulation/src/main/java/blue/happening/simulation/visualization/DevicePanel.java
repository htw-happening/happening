package blue.happening.simulation.visualization;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.RemoteDevice;
import blue.happening.simulation.entities.Device;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;
    private JTable table;
    private JButton btn_sendMessage;
    private Device device;
    private List<RemoteDevice> selectedDevices;

    public DevicePanel() {
        selectedDevices = new ArrayList<>();
        this.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.setLayout(new BorderLayout());
        JLabel lab1 = new JLabel("Current Device", JLabel.LEFT);
        JButton btn_disable = new JButton("Disable Device");
        btn_sendMessage = new JButton("Send message");
        btn_sendMessage.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(lab1);
        btnPanel.add(btn_disable);
        btnPanel.add((btn_sendMessage));
        this.add(btnPanel, BorderLayout.NORTH);


        table = new JTable();
        table.setAutoCreateRowSorter(true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        this.add(tableScrollPane, BorderLayout.CENTER);

        this.setVisible(true);

        btn_sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (RemoteDevice remotedevice : selectedDevices) {
                    device.sendMessageTo(remotedevice.getUuid(), "Hello".getBytes());
                }
            }
        });
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
        setNeighbourList(device);
    }

    public void updateDevice(Device device){
        DeviceNeighbourTableModel model = (DeviceNeighbourTableModel)table.getModel();
        model.update(device.getDevices());
        table.updateUI();
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
