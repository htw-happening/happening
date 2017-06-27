package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import blue.happening.simulation.entities.Device;


public class DeviceLabeler<V extends Device, String>
        implements Transformer<Device, String> {

    @Override
    public String transform(Device device) {
        return (String) device.toString().replaceFirst("^Device_", "");
    }
}
