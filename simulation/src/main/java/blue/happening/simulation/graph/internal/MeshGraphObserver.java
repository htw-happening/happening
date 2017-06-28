package blue.happening.simulation.graph.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;
import blue.happening.simulation.graph.NetworkGraphObserver;


public class MeshGraphObserver
        extends NetworkGraphObserver<Device, Connection> {

    private static Logger logger = LogManager.getLogger(MeshGraphObserver.class);

    @Override
    protected void addedEdge(final NetworkGraph<Device, Connection> networkGraph,
                             final Connection edge) {
        logger.debug("add edge from " + edge.getFromDevice() + " to " + edge.getToDevice());
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
