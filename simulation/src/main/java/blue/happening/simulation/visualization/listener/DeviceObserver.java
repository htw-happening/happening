package blue.happening.simulation.visualization.listener;

import java.util.Observable;
import java.util.Observer;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.NetworkGraph;


public class DeviceObserver implements Observer {
    private NetworkGraph<Device, Connection> graph;

    public DeviceObserver(NetworkGraph graph) {
        this.graph = graph;
    }

    public void update(Observable obj, Object arg) {
        Device device = (Device) obj;
        if (arg == "visualize passive connections") {
            /*for (UUID uuid : device.getHappeningMeshHandler().getPassiveConnectionDeviceList()) {
                for (Device d : blue.happening.bla.graph.getVertices()) {
                    if (d.getUUID().equals(uuid)) {
                        d.setIsAddedAsPassive(true);
                    }

                }
            }*/
        }
        if (arg == "receiving") {
            device.setIsReceiving(true);
            device.setIsSending(false);

        }
        if (arg == "sending") {
            device.setIsReceiving(false);
            device.setIsSending(true);

        }

    }
}
