package blue.happening.simulation.graph.internal;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.NetworkGraphObserver;


public class MeshGraphObserver
        extends NetworkGraphObserver<Device, Connection> {

    @Override
    protected void addedEdge(final NetworkGraph<Device, Connection> networkGraph,
                             final Connection edge) {
        System.out.println("added VVVV");
        edge.getFromDev().connectTo(edge.getToDev());
        edge.getToDev().connectTo(edge.getFromDev());
    }

    @Override
    protected void removedEdge(
            final NetworkGraph<Device, Connection> networkGraph,
            final Connection edge) {
        edge.getFromDev().disconnectFrom(edge.getToDev());
        edge.getToDev().disconnectFrom(edge.getFromDev());
    }

    @Override
    protected void addedVertex(
            final NetworkGraph<Device, Connection> networkGraph,
            final Device device) {
        System.out.println("added VVVV");

    }

    @Override
    protected void removedVertex(
            final NetworkGraph<Device, Connection> networkGraph,
            final Device device) {
        System.out.println("added VVVV");

    }
}
