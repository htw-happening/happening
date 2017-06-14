package blue.happening.mesh;

import java.util.HashSet;


public abstract class RemoteDevice implements Comparable<RemoteDevice> {

    private final String uuid;
    private long lastSeen;
    private SlidingWindow echoSlidingWindow;
    private SlidingWindow receiveSlidingWindow;

    private HashSet<String> neighbourUuids;

    public RemoteDevice(String uuid) {
        this.uuid = uuid;
        lastSeen = System.currentTimeMillis();
        neighbourUuids = new HashSet<>();
        echoSlidingWindow = new SlidingWindow();
        receiveSlidingWindow = new SlidingWindow();
    }

    public SlidingWindow getEchoSlidingWindow() {
        return echoSlidingWindow;
    }

    public SlidingWindow getReceiveSlidingWindow() {
        return receiveSlidingWindow;
    }

    public final String getUuid() {
        return uuid;
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

    final float getTq() {
        return 1;
//        return (echoSlidingWindow.size() / SlidingWindow.WINDOW_SIZE) *
//                (receiveSlidingWindow.size() / SlidingWindow.WINDOW_SIZE);
    }

    public abstract boolean sendMessage(Message message);

    public void remove() {
    }

    @Override
    public int compareTo(RemoteDevice other) {
        return Float.compare(
                this.getTq(),
                other.getTq());
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
