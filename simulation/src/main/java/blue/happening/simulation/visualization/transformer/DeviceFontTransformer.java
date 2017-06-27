package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import java.awt.Font;

import blue.happening.simulation.entities.Device;

public class DeviceFontTransformer<I extends Device, O extends Font>
        implements Transformer<Device, Font> {

    @Override
    public Font transform(Device device) {
        return new Font("Comic Sans MS", Font.BOLD, 14);
    }
}
