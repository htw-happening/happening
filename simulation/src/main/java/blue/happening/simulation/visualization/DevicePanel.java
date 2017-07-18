package blue.happening.simulation.visualization;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.RemoteDevice;
import blue.happening.mesh.statistics.Stat;
import blue.happening.mesh.statistics.StatsResult;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.entities.LogItem;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_HEIGHT = 200;
    private static final int TIME_WINDOW_SIZE = 25;
    private JLabel statsOgmIn, statsOgmOut, statsUcmIn, statsUcmOut;
    private JLabel deviceLabel;
    private JTable table;
    private JPanel tablePanel;
    private JTable ogmLogTable;
    private JTable ucmLogTable;
    private JButton sendButton;
    private JPanel logTablePanel;
    private JSlider packageDropSlider;
    private JSlider packageDelaySlider;
    private List<RemoteDevice> selectedDevices;
    private boolean messageCount = true;

    private Device device;
    private NetworkStatsPanel ogmNetworkStats;
    private NetworkStatsPanel ucmNetworkStats;

    DevicePanel() {
        selectedDevices = new ArrayList<>();
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JTabbedPane tabbedPane = new JTabbedPane();

        // Button Panel

        deviceLabel = new JLabel("Current device", JLabel.LEFT);
        JButton disableButton = new JButton("Toggle device");
        sendButton = new JButton("Send message");
        sendButton.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(deviceLabel);
        btnPanel.add(disableButton);
        btnPanel.add(sendButton);

        // Slider Panel

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setOpaque(false);

        packageDropSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        packageDropSlider.setMajorTickSpacing(10);
        packageDropSlider.setMinorTickSpacing(10);
        packageDropSlider.setPaintTicks(true);
        packageDropSlider.setPaintLabels(true);
        packageDropSlider.setOpaque(false);
        packageDropSlider.setEnabled(false);
        sliderPanel.add(new JLabel("Package Drop Rate", JLabel.CENTER));
        sliderPanel.add(packageDropSlider);

        packageDelaySlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
        packageDelaySlider.setMajorTickSpacing(100);
        packageDelaySlider.setMinorTickSpacing(100);
        packageDelaySlider.setPaintTicks(true);
        packageDelaySlider.setPaintLabels(true);
        packageDelaySlider.setOpaque(false);
        packageDelaySlider.setEnabled(false);
        sliderPanel.add(new JLabel("Package Send Delay", JLabel.CENTER));
        sliderPanel.add(packageDelaySlider);

        // Devices Panel

        table = new JTable();
        table.setAutoCreateRowSorter(true);

        tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(new JScrollPane(table));
        tablePanel.setVisible(false);
        tablePanel.setOpaque(false);

        JPanel devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
        devicesPanel.setOpaque(false);
        devicesPanel.add(btnPanel);
        devicesPanel.add(tablePanel);

        // Network Stats

        String[] settings = {"Number of Messages", "Size of Messages (bytes)"};

        JComboBox<String> selectStats = new JComboBox<>(settings);
        selectStats.setSelectedIndex(0);
        selectStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                messageCount = !messageCount;
                ogmNetworkStats.clear();
                ucmNetworkStats.clear();
            }
        });

        ogmNetworkStats = new NetworkStatsPanel(TIME_WINDOW_SIZE);
        ucmNetworkStats = new NetworkStatsPanel(TIME_WINDOW_SIZE);

        statsOgmIn = new JLabel();
        statsOgmOut = new JLabel();
        statsUcmIn = new JLabel();
        statsUcmOut = new JLabel();

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.add(selectStats);

        statsPanel.add(new JLabel("OGM Stats"));
        statsPanel.add(statsOgmIn);
        statsPanel.add(statsOgmOut);
        statsPanel.add(ogmNetworkStats.getChartPanel());

        statsPanel.add(new JLabel("UCM Stats"));
        statsPanel.add(statsUcmIn);
        statsPanel.add(statsUcmOut);
        statsPanel.add(ucmNetworkStats.getChartPanel());

        statsPanel.setVisible(false);

        // Message Logging

        ogmLogTable = new JTable();
        ogmLogTable.setAutoCreateRowSorter(true);

        ucmLogTable = new JTable();
        ucmLogTable.setAutoCreateRowSorter(true);

        logTablePanel = new JPanel();
        logTablePanel.setLayout(new BoxLayout(logTablePanel, BoxLayout.Y_AXIS));
        logTablePanel.add(new JLabel("OGM Logs"));
        logTablePanel.add(new JScrollPane(ogmLogTable));
        logTablePanel.add(new JLabel("UCM Logs"));
        logTablePanel.add(new JScrollPane(ucmLogTable));
        logTablePanel.setVisible(false);
        logTablePanel.setOpaque(false);

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setOpaque(false);
        logPanel.add(logTablePanel);

        // Tabs

        tabbedPane.addTab("Mesh Settings", sliderPanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Neighbour Devices", devicesPanel);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        tabbedPane.addTab("Network Stats", statsPanel);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        tabbedPane.addTab("Message Logs", logPanel);
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        add(tabbedPane);

        setVisible(true);

        packageDropSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (device != null) {
                    device.getMockLayer().setMessageLoss((float) source.getValue() / 100);
                }
            }
        });

        packageDelaySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (device != null) {
                    device.setMessageDelay(source.getValue());
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String s = (String) JOptionPane.showInputDialog(tabbedPane, "Your Message:", "Send Message", JOptionPane.PLAIN_MESSAGE, null, null, "Hallo");
                for (RemoteDevice remotedevice : selectedDevices) {
                    if (device != null) {
                        device.getMeshHandler().sendMessage(s.getBytes(), remotedevice.getUuid());
                    }
                }
            }
        });

        disableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (device != null) {
                    device.toggleEnabled();
                }
            }
        });
    }

    private void updateNetworkStats(StatsResult stats) {
        Stat ogmIn = stats.getOgmIncoming();
        Stat ogmOut = stats.getOgmOutgoing();

        Stat ucmIn = stats.getUcmIncoming();
        Stat ucmOut = stats.getUcmOutgoing();

        if (messageCount) {
            ogmNetworkStats.addValues(ogmIn.getMessageCountForTs(), ogmOut.getMessageCountForTs());
            ucmNetworkStats.addValues(ucmIn.getMessageCountForTs(), ucmOut.getMessageCountForTs());
        } else {
            ogmNetworkStats.addValues(ogmIn.getMessageSizeForTs(), ogmOut.getMessageSizeForTs());
            ucmNetworkStats.addValues(ucmIn.getMessageSizeForTs(), ucmOut.getMessageSizeForTs());
        }

        statsOgmIn.setText("Incoming Traffic: " +
                ogmIn.getMessageCountForTs() + "/" + (int) ogmIn.getTotalMessageCount() +
                " (" + Math.round(ogmIn.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ogmIn.getTotalMessageSize() / 1024) + "kb)");

        statsOgmOut.setText("Outgoing Traffic: " +
                ogmOut.getMessageCountForTs() + "/" + (int) ogmOut.getTotalMessageCount() +
                " (" + Math.round(ogmOut.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ogmOut.getTotalMessageSize() / 1024) + "kb)");

        statsUcmIn.setText("Incoming Traffic: " +
                ucmIn.getMessageCountForTs() + "/" + (int) ucmIn.getTotalMessageCount() +
                " (" + Math.round(ucmIn.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ucmIn.getTotalMessageSize() / 1024) + "kb)");

        statsUcmOut.setText("Outgoing Traffic: " +
                ucmOut.getMessageCountForTs() + "/" + (int) ucmOut.getTotalMessageCount() +
                " (" + Math.round(ucmOut.getMessageSizeForTs() / 1024) + "kb/" +
                (int) Math.round(ucmOut.getTotalMessageSize() / 1024) + "kb)");
    }

    private void updateMessageLossSlider(Device device) {
        int newVal = (int) (device.getMockLayer().getMessageLoss() * 100);
        if (newVal != packageDropSlider.getValue()) {
            packageDropSlider.setValue(newVal);
            packageDropSlider.updateUI();
        }
    }

    private void updatePackageDelay(Device device) {
        int newVal = device.getMessageDelay();
        if (newVal != packageDelaySlider.getValue()) {
            packageDelaySlider.setValue(newVal);
            packageDelaySlider.updateUI();
        }
    }

    private void setNeighbourList(Device device) {
        tablePanel.setVisible(true);
        List<MeshDevice> neighbours = device.getDevices();
        DeviceNeighbourTableModel neighbourTableModel = new DeviceNeighbourTableModel(neighbours);
        table.setModel(neighbourTableModel);
        table.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
    }

    private void addNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        neighbourTableModel.getNeighbours().add(neighbour);
        neighbourTableModel.fireTableDataChanged();
    }

    private void updateNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        List<MeshDevice> neighbours = neighbourTableModel.getNeighbours();
        int indexOfExisting = neighbours.indexOf(neighbour);
        try {
            neighbours.set(indexOfExisting, neighbour);
            neighbourTableModel.fireTableRowsUpdated(indexOfExisting, indexOfExisting);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void removeNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        neighbourTableModel.getNeighbours().remove(neighbour);
        neighbourTableModel.fireTableDataChanged();
    }

    private void setOgmLog(Device device) {
        DeviceLogTableModel deviceLogTableModel = new DeviceLogTableModel(device.getOgmLog().getLogs(), device);
        ogmLogTable.setModel(deviceLogTableModel);
        ogmLogTable.setVisible(true);
        ogmLogTable.updateUI();
    }

    private void updateOgmLog(LogItem log) {
        DeviceLogTableModel deviceLogTableModel = (DeviceLogTableModel) ogmLogTable.getModel();
        if (deviceLogTableModel.getLogs().contains(log)) {
            deviceLogTableModel.getLogs().set(deviceLogTableModel.getLogs().indexOf(log), log);
        } else {
            deviceLogTableModel.getLogs().add(log);
        }
        deviceLogTableModel.fireTableDataChanged();
    }

    private void setUcmLog(Device device) {
        DeviceLogTableModel deviceLogTableModel = new DeviceLogTableModel(device.getUcmLog().getLogs(), device);
        ucmLogTable.setModel(deviceLogTableModel);
        ucmLogTable.setVisible(true);
    }

    private void updateUcmLog(LogItem options) {

    }

    public void setDevice(Device device) {
        this.device = device;
        updateMessageLossSlider(device);
        updatePackageDelay(device);
        setNeighbourList(device);
        setOgmLog(device);
        clearNetworkStats();
        deviceLabel.setText(device.getName());
        logTablePanel.setVisible(true);
        packageDropSlider.setEnabled(true);
        packageDelaySlider.setEnabled(true);
    }

    private void clearNetworkStats() {
        ogmNetworkStats.clear();
        ucmNetworkStats.clear();
    }

    public void updateDevice(Device device, Device.DeviceChangedEvent event) {
        if (event != null) {
            switch (event.getType()) {
                case NEIGHBOUR_ADDED:
                    addNeighbour((MeshDevice) event.getOptions());
                    break;
                case NEIGHBOUR_UPDATED:
                    updateNeighbour((MeshDevice) event.getOptions());
                    break;
                case NEIGHBOUR_REMOVED:
                    removeNeighbour((MeshDevice) event.getOptions());
                    break;
                case NETWORK_STATS_UPDATED:
                    updateNetworkStats((StatsResult) event.getOptions());
                    break;
                case OGM_LOG_ITEM_ADDED:
                    updateOgmLog((LogItem) event.getOptions());
                    break;
                case UCM_LOG_ITEM_ADDED:
                    updateUcmLog((LogItem) event.getOptions());
                    break;
            }
        } else {
            setDevice(device);
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
