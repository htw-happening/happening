package blue.happening.simulation.graph.internal;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.EdgePool;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.mobility.MobilityPattern;


public class MeshGraph extends NetworkGraph<Device, Connection> {

    public MeshGraph() {
        super("Happening Mesh", new DeviceEdgePool());
        addObserver(new MeshGraphObserver());
    }

    private static class DeviceEdgePool implements EdgePool<Device, Connection> {

        @Override
        public Connection getEdge(Device fromDev, Device toDev) {
            return new Connection(fromDev, toDev);
        }
    }
}
