package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import java.awt.BasicStroke;
import java.awt.Stroke;

import blue.happening.simulation.entities.Connection;


public class ConnectionStrokeTransformer<I extends Connection, O extends Stroke>
        implements Transformer<Connection, Stroke> {

    final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f);
    final Stroke edgeStroke2 = new BasicStroke(4.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10.0f);

    @Override
    public Stroke transform(Connection connection) {

        return connection.getFromDev().isSending() && connection.getToDev()
                .isReceiving() ? edgeStroke2 : edgeStroke;
    }
}


