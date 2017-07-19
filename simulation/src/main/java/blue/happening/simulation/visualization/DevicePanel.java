package blue.happening.simulation.visualization;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.RemoteDevice;
import blue.happening.mesh.statistics.Stat;
import blue.happening.mesh.statistics.StatsResult;
import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.entities.LogItem;
import blue.happening.simulation.graph.NetworkGraph;


public class DevicePanel extends JPanel {

    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 1000;
    private static final int TIME_WINDOW_SIZE = 25;
    private TitledBorder title;
    private JPanel devicePanel;
    private JLabel statsOgmIn, statsOgmOut, statsUcmIn, statsUcmOut;
    private JLabel deviceLabel;
    private JTable table;
    private JPanel tablePanel;
    private JTable ogmLogTable;
    private JTable ucmLogTable;
    private JButton sendButton;
    private JButton resetButton;
    private JPanel logTablePanel;
    private JButton disableButton;
    private JSlider packageDropSlider;
    private JSlider packageDelaySlider;
    private List<RemoteDevice> selectedDevices;
    private boolean messageCount;

    private Device device;
    private HappeningDemo demo;
    private NetworkStatsPanel ogmNetworkStats;
    private NetworkStatsPanel ucmNetworkStats;
    private NetworkGraph<Device, Connection> graph;

    DevicePanel() {
        messageCount = true;
        selectedDevices = new ArrayList<>();
        demo = HappeningDemo.getInstance();
        graph = demo.getGraph();

        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JTabbedPane tabbedPane = new JTabbedPane();

        // Button Panel

        deviceLabel = new JLabel("Current device", JLabel.LEFT);
        disableButton = new JButton("Disable Device");
        sendButton = new JButton("Send Message");
        resetButton = new JButton("Reset Demo");
        sendButton = new JButton("Send message");
        sendButton.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(deviceLabel);
        btnPanel.add(disableButton);
        btnPanel.add(resetButton);
        btnPanel.add(sendButton);

        // Slider Panel

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setOpaque(false);
        sliderPanel.add(resetButton);

        devicePanel = new JPanel();
        devicePanel.setLayout(new BoxLayout(devicePanel, BoxLayout.Y_AXIS));
        devicePanel.setOpaque(false);

        title = BorderFactory.createTitledBorder("Device");
        devicePanel.setBorder(title);
        devicePanel.setVisible(false);

        devicePanel.add(disableButton, LEFT_ALIGNMENT);
        packageDropSlider = new JSlider(JSlider.HORIZONTAL,
                0, 100, 0);
        packageDropSlider.setMajorTickSpacing(10);
        packageDropSlider.setMinorTickSpacing(10);
        packageDropSlider.setPaintTicks(true);
        packageDropSlider.setPaintLabels(true);
        packageDropSlider.setOpaque(false);
        packageDropSlider.setEnabled(false);
        devicePanel.add(new JLabel("Package Drop Rate", JLabel.CENTER));
        devicePanel.add(packageDropSlider);

        packageDelaySlider = new JSlider(JSlider.HORIZONTAL,
                0, 1000, 0);
        packageDelaySlider.setMajorTickSpacing(100);
        packageDelaySlider.setMinorTickSpacing(100);
        packageDelaySlider.setPaintTicks(true);
        packageDelaySlider.setPaintLabels(true);
        packageDelaySlider.setOpaque(false);
        packageDelaySlider.setEnabled(false);
        devicePanel.add(new JLabel("Package Send Delay", JLabel.CENTER));
        devicePanel.add(packageDelaySlider);

        sliderPanel.add(devicePanel);

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
                clearNetworkStats();
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
        statsPanel.add(new JLabel("Y-Axis:"));
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

        tabbedPane.addTab("Settings", sliderPanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Devices", devicesPanel);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        tabbedPane.addTab("Stats", statsPanel);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        tabbedPane.addTab("Logs", logPanel);
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
                    disableButton.setText(device.isEnabled() ? "Disable Device" : "Enable Device");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                demo.reset();
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
        DeviceLogTableModel ogmLogTableModel = new DeviceLogTableModel(device.getOgmLog().getLogs(), device);
        ogmLogTable.setModel(ogmLogTableModel);
        ogmLogTable.setVisible(true);
        ogmLogTable.updateUI();
    }

    private void updateOgmLog(LogItem log) {
        DeviceLogTableModel ogmLogTableModel = (DeviceLogTableModel) ogmLogTable.getModel();
        if (ogmLogTableModel.getLogs().contains(log)) {
            ogmLogTableModel.getLogs().set(ogmLogTableModel.getLogs().indexOf(log), log);
        } else {
            ogmLogTableModel.getLogs().add(log);
        }
        ogmLogTableModel.fireTableDataChanged();
    }

    private void setUcmLog(Device device) {
        DeviceLogTableModel ucmLogTableModel = new DeviceLogTableModel(device.getUcmLog().getLogs(), device);
        ucmLogTable.setModel(ucmLogTableModel);
        ucmLogTable.setVisible(true);
        ucmLogTable.updateUI();
    }

    private void updateUcmLog(LogItem log) {
        DeviceLogTableModel ucmLogTableModel = (DeviceLogTableModel) ucmLogTable.getModel();
        if (ucmLogTableModel.getLogs().contains(log)) {
            ucmLogTableModel.getLogs().set(ucmLogTableModel.getLogs().indexOf(log), log);
        } else {
            ucmLogTableModel.getLogs().add(log);
        }
        ucmLogTableModel.fireTableDataChanged();

    }

    public void setDevice(Device device) {
        this.device = device;
        updateMessageLossSlider(device);
        updatePackageDelay(device);
        setNeighbourList(device);
        setOgmLog(device);
        setUcmLog(device);
        clearNetworkStats();
        deviceLabel.setText(device.getName());
        title.setTitle(device.getName());
        devicePanel.updateUI();
        devicePanel.setVisible(true);
        logTablePanel.setVisible(true);
        packageDropSlider.setEnabled(true);
        packageDelaySlider.setEnabled(true);
        disableButton.setText(device.isEnabled() ? "Disable Device" : "Enable Device");
    }

    private void clearNetworkStats() {
        ogmNetworkStats.clear();
        ucmNetworkStats.clear();
    }

    public void updateDevice(Device device, Device.DeviceChangedEvent event) {
        if (event != null) {
            try {
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
            } catch (ClassCastException e) {
                e.printStackTrace();
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
