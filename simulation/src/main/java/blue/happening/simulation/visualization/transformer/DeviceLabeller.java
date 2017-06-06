package blue.happening.simulation.visualization.transformer;

import org.apache.commons.collections15.Transformer;

import blue.happening.simulation.entities.Device;


public class DeviceLabeller<V extends Device, String>
        implements Transformer<Device, String> {

    public String transform(Device device) {

        return (String) device.toString();
    }
}
