package blue.happening.simulation.visualization;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import blue.happening.mesh.MeshDevice;

public class DeviceNeighbourTableModel extends AbstractTableModel {
    //Two arrays used for the table data
    private String[] columnNames = {"Identifier", "Quality", "Seconds ago"};
    private Class[] columnClasses = {String.class, Float.class, Integer.class};

    private List<MeshDevice> neighbours = new ArrayList<>();

    public DeviceNeighbourTableModel(List<MeshDevice> neighbours) {
        this.neighbours.addAll(neighbours);
    }

    public List<MeshDevice> getNeighbours() {
        return neighbours;
    }

    @Override
    public int getRowCount() {
        return neighbours.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MeshDevice neighbour;
        try {
            neighbour = neighbours.get(row);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        if (neighbour == null) {
            return null;
        }
        switch (column) {
            case 0:
                return neighbour.getUuid();
            case 1:
                return neighbour.getQuality();
            case 2:
                return (Math.round((System.currentTimeMillis() - neighbour.getLastSeen()) / 1000));
            default:
                return null;
        }
    }

    // Used by the JTable object to set the column names
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Used by the JTable object to render functionality based on data type
    @Override
    public Class getColumnClass(int column) {
        return columnClasses[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
