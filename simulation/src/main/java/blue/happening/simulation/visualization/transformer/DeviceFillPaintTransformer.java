package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import java.awt.Color;
import java.awt.Paint;

import blue.happening.simulation.entities.Device;


public class DeviceFillPaintTransformer<I extends Device, O extends Paint>
        implements Transformer<Device, Paint> {

    @Override
    public Paint transform(Device device) {
        if (!device.isEnabled()) {
            return Color.RED;
        } else if (device.isClicked()) {
            return Color.GREEN;
        } else if (device.isNeighbour()) {
            return Color.BLUE;
        } else {
            return Color.GRAY;
        }
    }
}
