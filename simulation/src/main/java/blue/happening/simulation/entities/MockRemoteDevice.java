package blue.happening.simulation.entities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import blue.happening.mesh.Message;
import blue.happening.mesh.RemoteDevice;


public class MockRemoteDevice extends RemoteDevice {

    private Device device;

    private static ScheduledExecutorService executor;

    public MockRemoteDevice(String uuid) {
        super(uuid);
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    private void simulateSendDelay(Runnable runnable, int delayInMs){
        executor.schedule(runnable, delayInMs, TimeUnit.MILLISECONDS);
    }

    public boolean sendMessage(final Message message) {
        device.addToOutbox(message);
        simulateSendDelay(new Runnable() {
            @Override
            public void run() {
                device.getMockLayer().sendMessage(message);
                device.removeFromOutBox(message);
            }
        }, 100);

        return true;
    }

    @Override
    public boolean remove() {
        /*
        for (Object edge : new ArrayList<>(device.getNetworkGraph().getInEdges(device))) {
            device.getNetworkGraph().removeEdge(edge);
        }
        VertexProperties device.getNetworkGraph().getVerticesProperties().get(device)
        */
        // TODO: Handle broken connections and inform bla
        return false;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
