package blue.happening.simulation.graph;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;


class MeshGraphObserver extends NetworkGraphObserver<Device, Connection> {

    @Override
    protected void addedEdge(
            final NetworkGraph<Device, Connection> networkGraph,
            final Connection edge) {
        edge.getFromDevice().connectTo(edge.getToDevice());
        edge.getToDevice().connectTo(edge.getFromDevice());
    }

    @Override
    protected void removedEdge(
            final NetworkGraph<Device, Connection> networkGraph,
            final Connection edge) {
        edge.getFromDevice().disconnectFrom(edge.getToDevice());
        edge.getToDevice().disconnectFrom(edge.getFromDevice());
    }
}
