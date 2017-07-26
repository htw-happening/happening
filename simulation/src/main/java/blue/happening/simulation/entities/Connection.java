package blue.happening.simulation.entities;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import blue.happening.mesh.Message;
import blue.happening.simulation.demo.HappeningDemo;
import blue.happening.simulation.graph.MeshGraph;

public class Connection {

    public static final int IDLE = 0;
    public static final int SEND = 1;
    public static final int LOSS = 2;
    private final Device fromDevice;
    private final Device toDevice;
    private final Queue<MessageDelivery> deliverance;

    public Connection(Device fromDevice, Device toDevice) {
        this.fromDevice = fromDevice;
        this.toDevice = toDevice;
        this.deliverance = new LinkedBlockingQueue<>();
    }

    public Device getFromDevice() {
        return fromDevice;
    }

    public Device getToDevice() {
        return toDevice;
    }

    private void purge() {
        Iterator<MessageDelivery> i = deliverance.iterator();
        while (i.hasNext()) {
            MessageDelivery delivery = i.next();
            if (delivery.isComplete()) {
                i.remove();
            }
        }
    }

    public void destroy() {
        deliverance.clear();
    }

    public int getStatus() {
        purge();
        MeshGraph graph = HappeningDemo.getGraph();
        Device clickedDevice = graph == null ? null : graph.getClickedDevice();
        MessageDelivery delivery = deliverance.peek();
        if (delivery == null) {
            return IDLE;
        } else if (clickedDevice == null) {
            if (delivery.isLost()) {
                return LOSS;
            } else {
                return SEND;
            }
        } else if (clickedDevice.getName().equals(delivery.getMessage().getSource())) {
            if (delivery.isLost()) {
                return LOSS;
            } else {
                return SEND;
            }
        }
        return IDLE;
    }

    void queueMessage(Message message) {
        final byte[] bytes = message.toBytes();
        final boolean lost = Math.random() < fromDevice.getMockLayer().getMessageLoss();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!lost) {
                    toDevice.getMockLayer().getLayerCallback().onMessageReceived(bytes);
                }
            }
        };

        int delay = getFromDevice().getMessageDelay();
        for (MessageDelivery delivery : deliverance) {
            delay += delivery.getDelay();
        }
        ScheduledFuture future = getToDevice().getRunner().schedule(runnable, delay, TimeUnit.MILLISECONDS);
        deliverance.offer(new MessageDelivery(future, message, lost));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
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

    private class MessageDelivery implements Comparable<MessageDelivery> {
        private ScheduledFuture future;
        private Message message;
        private boolean lost;

        MessageDelivery(ScheduledFuture future, Message message, boolean lost) {
            this.future = future;
            this.message = message;
            this.lost = lost;
        }

        boolean isComplete() {
            return (future.isDone() || future.isCancelled());
        }

        long getDelay() {
            return future.getDelay(TimeUnit.MILLISECONDS);
        }

        Message getMessage() {
            return message;
        }

        boolean isLost() {
            return lost;
        }

        @Override
        public int compareTo(MessageDelivery other) {
            return Long.compare(getDelay(), other.getDelay());
        }
    }
}
