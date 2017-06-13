package blue.happening.simulation.graph.internal;

import org.apache.log4j.Logger;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.NetworkGraphObserver;


public class MeshGraphObserver
        extends NetworkGraphObserver<Device, Connection> {

    private static Logger logger = Logger.getLogger(MeshGraphObserver.class);

    @Override
    protected void addedEdge(final NetworkGraph<Device, Connection> networkGraph,
                             final Connection edge) {
        logger.debug("add edge from " + edge.getFromDev() + " to " + edge.getToDev());
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
        logger.debug("add vertex for device: " + device);

    }

    @Override
    protected void removedVertex(
            final NetworkGraph<Device, Connection> networkGraph,
            final Device device) {
        logger.debug("remove vertex for device: " + device);

    }
}
