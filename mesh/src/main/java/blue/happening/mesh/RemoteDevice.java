package blue.happening.mesh;

import java.util.HashSet;


public abstract class RemoteDevice implements Comparable<RemoteDevice> {

    private final String uuid;
    private long lastSeen;
    private float echoQuality;
    private float receiveQuality;
    private SlidingWindow slidingWindow;

    private HashSet<String> neighbourUuids;

    public RemoteDevice(String uuid) {
        this.uuid = uuid;
        lastSeen = System.currentTimeMillis();
        echoQuality = 1;
        receiveQuality = 1;
        neighbourUuids = new HashSet<>();
        slidingWindow = new SlidingWindow();
    }

    public final String getUuid() {
        return uuid;
    }

    public SlidingWindow getSlidingWindow() {
        return slidingWindow;
    }

    private float getTransmissionQuality() {
        return getEchoQuality() / getReceiveQuality();
    }

    public float getEchoQuality() {
        return echoQuality;
    }

    public float getReceiveQuality() {
        //return (float) slidingWindow.size() / (float) SlidingWindow.WINDOW_SIZE;
        return 1;
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

    public abstract void sendMessage(Message message);

    public void remove() {
    }

    @Override
    public int compareTo(RemoteDevice other) {
        return Float.compare(
                this.getTransmissionQuality(),
                other.getTransmissionQuality());
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
        return this.getClass().getSimpleName() + "@" + getUuid();
    }
}
