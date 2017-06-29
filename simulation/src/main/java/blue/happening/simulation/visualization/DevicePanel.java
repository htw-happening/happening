package blue.happening.simulation.visualization;

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
import blue.happening.simulation.visualization.listener.DeviceObserver;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;
    private JTable table;
    private JLabel deviceLabel;
    private JButton sendButton;
    private JButton disableButton;
    private JSlider packageDropSlider;
    private JSlider packageDelaySlider;
    private Device device;
    private List<RemoteDevice> selectedDevices;

    public DevicePanel() {
        selectedDevices = new ArrayList<>();
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        deviceLabel = new JLabel("Current device", JLabel.LEFT);
        disableButton = new JButton("Toggle device");
        sendButton = new JButton("Send message");
        sendButton.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(deviceLabel);
        btnPanel.add(disableButton);
        btnPanel.add(sendButton);
        add(btnPanel);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        packageDropSlider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 0);
        packageDropSlider.setMajorTickSpacing(10);
        packageDropSlider.setMinorTickSpacing(10);
        packageDropSlider.setPaintTicks(true);
        packageDropSlider.setPaintLabels(true);
        sliderPanel.add(new JLabel("Package Drop Rate", JLabel.CENTER));
        sliderPanel.add(packageDropSlider);

        packageDelaySlider = new JSlider(JSlider.HORIZONTAL,
                0, 1000, 0);
        packageDelaySlider.setMajorTickSpacing(100);
        packageDelaySlider.setMinorTickSpacing(100);
        packageDelaySlider.setPaintTicks(true);
        packageDelaySlider.setPaintLabels(true);
        sliderPanel.add(new JLabel("Package Send Delay", JLabel.CENTER));
        sliderPanel.add(packageDelaySlider);

        add(sliderPanel);

        table = new JTable();
        table.setAutoCreateRowSorter(true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane);

        setVisible(true);

        packageDropSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                device.getMockLayer().setMessageLoss((float) source.getValue() / 100);
            }
        });

        packageDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                device.setMessageDelay(source.getValue());
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (RemoteDevice remotedevice : selectedDevices) {
                    device.getMeshHandler().sendMessage("Hello".getBytes(), remotedevice.getUuid());
                }
            }
        });

        disableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                device.toggleEnabled();
            }
        });
    }

    public void updateMessageLossSlider(Device device) {
        int newVal = (int) (device.getMockLayer().getMessageLoss() * 100);
        if (newVal != packageDropSlider.getValue()) {
            packageDropSlider.setValue(newVal);
            packageDropSlider.updateUI();
        }
    }

    public void updatePackageDelay(Device device) {
        int newVal = (int) (device.getMessageDelay());
        if (newVal != packageDelaySlider.getValue()) {
            packageDelaySlider.setValue(newVal);
            packageDelaySlider.updateUI();
        }
    }

    public void setNeighbourList(Device device) {
        List<MeshDevice> neighbours = device.getDevices();
        DeviceNeighbourTableModel neighbourTableModel = new DeviceNeighbourTableModel(neighbours);
        table.setModel(neighbourTableModel);
        table.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
        table.updateUI();
    }

    public void addNeighbour(MeshDevice neighbour){
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel)table.getModel();
        neighbourTableModel.getNeighbours().add(neighbour);
        table.updateUI();
    }

    public void updateNeighbour(MeshDevice neighbour){
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel)table.getModel();
        List<MeshDevice> neighbours = neighbourTableModel.getNeighbours();
        int indexOfExisting = neighbours.indexOf(neighbour);
        neighbours.set(indexOfExisting, neighbour);
        table.updateUI();
    }

    public void removeNeighbour(MeshDevice neighbour){
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel)table.getModel();
        neighbourTableModel.getNeighbours().remove(neighbour);
        table.updateUI();
    }

    public void setDevice(Device device) {
        this.device = device;
        updateMessageLossSlider(device);
        updatePackageDelay(device);
        setNeighbourList(device);
        deviceLabel.setText(device.getName());
    }

    public void updateDevice(Device device, Device.DeviceChangedEvent event) {
        if(event == null){
            setNeighbourList(device);
            updateMessageLossSlider(device);
            updatePackageDelay(device);
        } else if(event.getType() == DeviceObserver.Events.NEIGHBOUR_ADDED){
            addNeighbour((MeshDevice) event.getOptions());
        } else if(event.getType() == DeviceObserver.Events.NEIGHBOUR_UPDATED){
            updateNeighbour((MeshDevice) event.getOptions());
        } else if(event.getType() == DeviceObserver.Events.NEIGHBOUR_REMOVED){
            removeNeighbour((MeshDevice) event.getOptions());
        }
    }

    private void setSelectedDevicesFromSelectedDeviceNames(List<String> selectedDeviceNames) {
        selectedDevices.clear();
        for (String name : selectedDeviceNames) {
            RemoteDevice selectedDevice = device.getMeshHandler().getRoutingTable().get(name);
            selectedDevices.add(selectedDevice);
        }
        if (selectedDevices.size() > 0) {
            sendButton.setEnabled(true);
        } else {
            sendButton.setEnabled(false);
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
