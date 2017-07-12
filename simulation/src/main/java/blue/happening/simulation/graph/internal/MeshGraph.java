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

    @Override
    public boolean addVertex(Device vertex, double sx, double sy, MobilityPattern<Device, Connection> mobilityPattern, double txRadius, double rxRadius) {
        vertex.setTxRadius(txRadius);
        vertex.setRxRadius(rxRadius);
        return super.addVertex(vertex, sx, sy, mobilityPattern, 0, 0);
    }

    private static class DeviceEdgePool implements EdgePool<Device, Connection> {

        @Override
        public Connection getEdge(Device fromDev, Device toDev) {
            return new Connection(fromDev, toDev);
        }
    }
}
