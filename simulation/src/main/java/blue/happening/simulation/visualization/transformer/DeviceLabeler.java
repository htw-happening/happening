package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import blue.happening.simulation.entities.Device;


public class DeviceLabeler implements Transformer<Device, String> {

    @Override
    public String transform(Device device) {
        return device.toString().replaceFirst("^device_", "");
    }
}
