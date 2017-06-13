package blue.happening.mesh;

import java.util.HashSet;


public abstract class RemoteDevice implements Comparable<RemoteDevice> {

    private final String uuid;
    private long lastSeen;
    private SlidingWindow slidingWindow;

    private HashSet<String> neighbourUuids;

    public RemoteDevice(String uuid) {
        this.uuid = uuid;
        lastSeen = System.currentTimeMillis();
        neighbourUuids = new HashSet<>();
        slidingWindow = new SlidingWindow(uuid);
    }

    public final String getUuid() {
        return uuid;
    }

    SlidingWindow getSlidingWindow() {
        return slidingWindow;
    }

    public final long getLastSeen() {
        return lastSeen;
    }

    public final void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public final boolean isExpired() {
        long expirationMillis = MeshHandler.DEVICE_EXPIRATION_DURATION * 1000;
        return System.currentTimeMillis() - lastSeen > expirationMillis;
    }

    public final HashSet<String> getNeighbourUuids() {
        return neighbourUuids;
    }

    public final boolean isNeighbour() {
        return neighbourUuids.contains(uuid);
    }

    public void mergeNeighbours(RemoteDevice remoteDevice) {
        neighbourUuids.addAll(remoteDevice.getNeighbourUuids());
    }

    public abstract boolean sendMessage(Message message);

    public void remove() {
    }

    @Override
    public int compareTo(RemoteDevice other) {
        return Float.compare(
                this.getSlidingWindow().getTransmissionQuality(),
                other.getSlidingWindow().getTransmissionQuality());
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (!(object instanceof RemoteDevice)) {
            return false;
        }
        return ((RemoteDevice) object).getUuid().equals(getUuid());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + getUuid();
    }
}
