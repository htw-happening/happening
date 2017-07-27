package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import java.awt.Color;
import java.awt.Paint;

import blue.happening.simulation.entities.Device;


public class DeviceFillPaintTransformer implements Transformer<Device, Paint> {

    @Override
    public Paint transform(Device device) {
        if (!device.isEnabled()) {
            return new Color(208, 26, 65);
        } else if (device.isClicked()) {
            return new Color(85, 165, 50);
        } else if (device.isNeighbour()) {
            return new Color(5, 140, 185);
        } else {
            return Color.GRAY;
        }
    }
}
