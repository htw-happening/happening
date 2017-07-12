package blue.happening.simulation.visualization;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
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

    private JPanel tablePanel;
    private NetworkStatsPanel ogmNetworkStats;
    private NetworkStatsPanel ucmNetworkStats;
    private List<Double> ogmIn = new LinkedList<>();
    private List<Double> ogmOut = new LinkedList<>();
    private List<Double> ucmIn = new LinkedList<>();
    private List<Double> ucmOut = new LinkedList<>();
    private boolean packageSize = true;
    private boolean packageCount;

    DevicePanel() {
        selectedDevices = new ArrayList<>();
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JTabbedPane tabbedPane = new JTabbedPane();

        // Button Panel

        deviceLabel = new JLabel("Current device", JLabel.LEFT);
        disableButton = new JButton("Toggle device");
        sendButton = new JButton("Send message");
        sendButton.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(deviceLabel);
        btnPanel.add(disableButton);
        btnPanel.add(sendButton);

        // Slider Panel

        final JPanel sliderPanel = new JPanel();
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

        // Devices Panel

        table = new JTable();
        table.setAutoCreateRowSorter(true);

        tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.add(new JLabel("Devices"));
        tablePanel.add(new JScrollPane(table));
        tablePanel.setVisible(false);

        JPanel devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
        devicesPanel.add(btnPanel);
        devicesPanel.add(tablePanel);

        // Network Stats

        String[] settings = {"Data", "Packages"};

        JComboBox<String> selectStats = new JComboBox<>(settings);
        selectStats.setSelectedIndex(0);
        selectStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                packageCount = !packageCount;
                packageSize = !packageSize;
            }
        });

        ogmNetworkStats = new NetworkStatsPanel(ogmIn, ogmOut);
        ucmNetworkStats = new NetworkStatsPanel(ucmIn, ucmOut);

        final JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.add(new JLabel("Network Stats"), JLabel.CENTER);
        statsPanel.add(selectStats);
        statsPanel.add(ogmNetworkStats);
        //statsPanel.add(ucmNetworkStats);
        statsPanel.setVisible(false);

        // Message Logging

        JPanel logPanel = new JPanel();
        logPanel.setOpaque(true);
        logPanel.setBackground(null);

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
                String s = (String) JOptionPane.showInputDialog(tabbedPane, "Your Message:", "Send Message", JOptionPane.PLAIN_MESSAGE, null, null, "Hallo");
                for (RemoteDevice remotedevice : selectedDevices) {
                    device.getMeshHandler().sendMessage(s.getBytes(), remotedevice.getUuid());
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

    private void updateNetworkStats(StatsResult stats) {
        Stat ogmIncoming = stats.getOgmIncoming();
        Stat ogmOutgoing = stats.getOgmOutgoing();

        Stat ucmIncoming = stats.getUcmIncoming();
        Stat ucmOutgoing = stats.getUcmOutgoing();

        updateNetworkStats(ogmIncoming, ogmOutgoing, ogmIn, ogmOut, ogmNetworkStats, 20);
        updateNetworkStats(ucmIncoming, ucmOutgoing, ucmIn, ucmOut, ucmNetworkStats, 20);

        /*
        String ogmIncomingTxt = "OGM Incoming Traffic: " +
                ogmIncoming.getMessageCountForTs() + "/" + ogmIncoming.getTotalMessageCount() +
                " (" + Math.round(ogmIncoming.getTotalMessageSize() / 1024) + "kb)";
        String ogmOutgoingTxt = "OGM Outgoing Traffic: " +
                ogmOutgoing.getMessageCountForTs() + "/" + ogmOutgoing.getTotalMessageCount() +
                " (" + Math.round(ogmOutgoing.getTotalMessageSize() / 1024) + "kb)";
        System.out.println(ogmIncomingTxt);
        System.out.println(ogmOutgoingTxt);
        */
    }

    private void updateNetworkStats(Stat in, Stat out, List<Double> inList, List<Double> outList,
                                    NetworkStatsPanel statsPanel, int windowSize) {
        if (inList.size() > windowSize)
            inList.remove(0);
        if (outList.size() > windowSize)
            outList.remove(0);
        if (packageCount) {
            inList.add((double) in.getMessageCountForTs());
            outList.add((double) out.getMessageCountForTs());
        } else if (packageSize) {
            inList.add((double) in.getMessageSizeForTs());
            outList.add((double) out.getMessageSizeForTs());
        }
        statsPanel.setValues(inList, outList);
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
        List<MeshDevice> neighbours = device.getDevices();
        DeviceNeighbourTableModel neighbourTableModel = new DeviceNeighbourTableModel(neighbours);
        table.setModel(neighbourTableModel);
        table.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
        table.updateUI();
        tablePanel.setVisible(true);
    }

    private void addNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        neighbourTableModel.getNeighbours().add(neighbour);
        table.updateUI();
    }

    private void updateNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        List<MeshDevice> neighbours = neighbourTableModel.getNeighbours();
        int indexOfExisting = neighbours.indexOf(neighbour);
        try {
            neighbours.set(indexOfExisting, neighbour);
            table.updateUI();
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    private void removeNeighbour(MeshDevice neighbour) {
        DeviceNeighbourTableModel neighbourTableModel = (DeviceNeighbourTableModel) table.getModel();
        neighbourTableModel.getNeighbours().remove(neighbour);
        table.updateUI();
    }

    public void setDevice(Device device) {
        this.device = device;
        updateMessageLossSlider(device);
        updatePackageDelay(device);
        setNeighbourList(device);
        clearNetworkStats();
        deviceLabel.setText(device.getName());
    }

    private void clearNetworkStats() {
        ogmIn.clear();
        ogmOut.clear();
        ucmIn.clear();
        ucmOut.clear();
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
