package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import blue.happening.simulation.entities.Connection;


public class ConnectionLabeller<V extends Connection, String>
        implements Transformer<Connection, String> {

    public String transform(Connection connection) {

        return (String) connection.toString();
    }
}
