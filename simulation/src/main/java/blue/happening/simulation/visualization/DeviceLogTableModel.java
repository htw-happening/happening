package blue.happening.simulation.visualization;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import blue.happening.mesh.MeshDevice;
import blue.happening.simulation.entities.LogItem;

public class DeviceLogTableModel extends AbstractTableModel {
    //Two arrays used for the table data
    private String[] columnNames = {"Source", "Previous Hop", "Destination", "#", "TTL", "TQ"};

    private List<LogItem> logs = new ArrayList<>();

    public DeviceLogTableModel(List<LogItem> logs) {
        this.logs.addAll(logs);
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
        switch (column) {
            case 0:
                return logItem.getMessage().getSource();
            case 1:
                return logItem.getMessage().getPreviousHop();
            case 2:
                return logItem.getMessage().getDestination();
            case 3:
                return logItem.getMessage().getSequence();
            case 4:
                return logItem.getMessage().getTtl();
            case 5:
                return logItem.getMessage().getTq();
            default:
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
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
