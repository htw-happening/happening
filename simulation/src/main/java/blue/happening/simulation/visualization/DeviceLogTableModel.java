package blue.happening.simulation.visualization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import blue.happening.mesh.MeshHandler;
import blue.happening.mesh.Message;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.entities.LogItem;

public class DeviceLogTableModel extends AbstractTableModel {
    // Two arrays used for the table data
    private String[] columnNames = {"Action", "Source", "Previous", "Destination", "Sequence", "TTL", "TQ"};
    private Class[] columnClasses = {String.class, String.class, String.class, String.class,
            Integer.class, Integer.class, Integer.class};

    private List<LogItem> logs = new ArrayList<>();
    private Device device;

    public DeviceLogTableModel(List<LogItem> logs, Device device) {
        this.logs.addAll(logs);
        this.device = device;
    }

    public List<LogItem> getLogs() {
        return logs;
    }

    @Override
    public int getRowCount() {
        return logs.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    private List<String> getDropReasons(Message message) {
        List<String> reasons = new ArrayList<>();
        if (message.getTq() <= 0) {
            reasons.add("TQ");
        }
        if (message.getTtl() <= 0) {
            reasons.add("TTL");
        }
        if (message.getSource().equals(device.getName())) {
            reasons.add("ECHO");
        }
        return reasons;
    }

    @Override
    public Object getValueAt(int row, int column) {
        LogItem logItem;
        try {
            logItem = logs.get(row);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        if (logItem == null) {
            return null;
        }
        Message message = logItem.getMessage();
        switch (column) {
            case 0:
                switch (logItem.getStatus()) {
                    case MeshHandler.MESSAGE_ACTION_DROPPED:
                        List<String> dropReasons = getDropReasons(message);
                        String reasons = "";
                        Iterator<String> i = dropReasons.iterator();
                        while (i.hasNext()) {
                            reasons += i.next();
                            if (i.hasNext()) {
                                reasons += ", ";
                            }
                        }
                        return "DROP" + (dropReasons.size() == 0 ? "" : " (" + reasons + ")");
                    case MeshHandler.MESSAGE_ACTION_FORWARDED:
                        if (message.getType() == MeshHandler.MESSAGE_TYPE_OGM &&
                                message.getPreviousHop().equals(message.getSource())) {
                            return "ECHO";
                        } else {
                            return "FORWARD";
                        }
                    case MeshHandler.MESSAGE_ACTION_ARRIVED:
                        return "ARRIVE";
                    case MeshHandler.MESSAGE_ACTION_RECEIVED:
                        return "RECEIVE";
                    case MeshHandler.MESSAGE_ACTION_SENT:
                        return "SEND";
                    default:
                        return "";
                }
            case 1:
                return message.getSource();
            case 2:
                return message.getPreviousHop();
            case 3:
                return message.getDestination();
            case 4:
                return message.getSequence();
            case 5:
                return message.getTtl();
            case 6:
                return message.getTq();
            default:
                System.out.println("NOPE");
                return null;
        }
    }

    //Used by the JTable object to set the column names
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    //Used by the JTable object to render different
    //functionality based on the data type
    @Override
    public Class getColumnClass(int column) {
        return columnClasses[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
