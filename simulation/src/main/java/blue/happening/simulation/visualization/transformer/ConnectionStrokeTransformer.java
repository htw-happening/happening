package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import java.awt.BasicStroke;
import java.awt.Stroke;

import blue.happening.simulation.entities.Connection;


public class ConnectionStrokeTransformer<I extends Connection, O extends Stroke>
        implements Transformer<Connection, Stroke> {

    private final Stroke noStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f);
    private final Stroke thinStroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f);
    private final Stroke thickStroke = new BasicStroke(4.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f);

    @Override
    public Stroke transform(Connection connection) {
        switch (connection.getStatus()) {
            case Connection.IDLE:
                return thinStroke;
            case Connection.SEND:
                return thickStroke;
            case Connection.LOSS:
                return noStroke;
            default:
                return thinStroke;
        }
    }
}
