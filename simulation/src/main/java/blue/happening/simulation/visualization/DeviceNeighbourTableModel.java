package blue.happening.simulation.visualization;

import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import blue.happening.mesh.MeshDevice;
import blue.happening.mesh.RemoteDevice;

public class DeviceNeighbourTableModel extends AbstractTableModel{
    //Two arrays used for the table data
    private String[] columnNames = {"UUID", "TQ", "Last Seen"};

    private Object[][] data = null;

    public DeviceNeighbourTableModel(){
        super();
    }

    public DeviceNeighbourTableModel(List<MeshDevice> neighbours){
        Object[][] neighbourTableEntries = new Object[neighbours.size()][3];
        int index = 0;
        for(MeshDevice neighbour: neighbours){
            Object[] entry = {
              neighbour.getUuid(),
              neighbour.getQuality(),
              neighbour.getLastSeen()
            };
            neighbourTableEntries[index++] = entry;
        }
        this.data = neighbourTableEntries;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return data[row][column];
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
