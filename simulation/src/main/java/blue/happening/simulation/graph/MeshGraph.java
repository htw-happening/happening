package blue.happening.simulation.graph;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.visualization.NOOPAction;


public class MeshGraph extends NetworkGraph<Device, Connection> {

    private Device clickedDevice = null;

    public MeshGraph(double noopInterval, long noopSleep) {
        super("Happening Mesh", new DeviceEdgePool());
        new NOOPAction(this, noopInterval, noopSleep);
        addObserver(new MeshGraphObserver());
    }

    private static class DeviceEdgePool implements EdgePool<Device, Connection> {

        @Override
        public Connection getEdge(Device fromDev, Device toDev) {
            return new Connection(fromDev, toDev);
        }
    }

    public Device getClickedDevice() {
        return clickedDevice;
    }

    public void setClickedDevice(Device clickedDevice) {
        this.clickedDevice = clickedDevice;
    }
}
