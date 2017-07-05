package blue.happening.simulation.graph.internal;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.NetworkGraphObserver;


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
