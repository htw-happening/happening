package blue.happening.simulation.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Connection {

    private Device fromDevice;
    private Device toDevice;
    private List<ScheduledFuture> queue;

    public Connection(Device fromDevice, Device toDevice) {
        this.fromDevice = fromDevice;
        this.toDevice = toDevice;
        this.queue = new ArrayList<>();
    }

    public Device getFromDevice() {
        return fromDevice;
    }

    public Device getToDevice() {
        return toDevice;
    }

    public boolean isTransmitting() {
        Iterator<ScheduledFuture> i = queue.iterator();
        while (i.hasNext()) {
            ScheduledFuture future = i.next();
            if (future.isDone() || future.isCancelled()) {
                i.remove();
            } else {
                return true;
            }
        }
        return false;
    }

    void queueBytes(final byte[] bytes) {
        if (Math.random() < fromDevice.getMockLayer().getMessageLoss()) {
            return;
        }
        int delay = getFromDevice().getMessageDelay();
        for (ScheduledFuture future : queue) {
            delay += future.getDelay(TimeUnit.MILLISECONDS);
        }

        queue.add(getToDevice().getPostman().schedule(new Runnable() {
            @Override
            public void run() {
                toDevice.getMockLayer().getLayerCallback().onMessageReceived(bytes);
            }
        }, delay, TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        return fromDevice.getName() + " → " + toDevice.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Connection))
            return false;
        else if (o == this)
            return true;
        return this.getFromDevice().equals(((Connection) o).getFromDevice()) &&
                this.getToDevice().equals(((Connection) o).getToDevice());
    }
}
