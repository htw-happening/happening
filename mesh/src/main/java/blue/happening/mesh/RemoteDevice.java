package blue.happening.mesh;

import java.util.HashSet;


public abstract class RemoteDevice implements IRemoteDevice {

    private final String uuid;
    private long lastSeen;
    private SlidingWindow echoSlidingWindow;
    private SlidingWindow receiveSlidingWindow;
    private MeshDevice meshDevice;

    public RemoteDevice(String uuid) {
        this.uuid = uuid;
        meshDevice = new MeshDevice();
        meshDevice.setUuid(uuid);
        lastSeen = System.currentTimeMillis();
        echoSlidingWindow = new SlidingWindow();
        receiveSlidingWindow = new SlidingWindow();
    }

    SlidingWindow getEchoSlidingWindow() {
        return echoSlidingWindow;
    }

    SlidingWindow getReceiveSlidingWindow() {
        return receiveSlidingWindow;
    }

    public final String getUuid() {
        return uuid;
    }

    long getLastSeen() {
        return lastSeen;
    }

    void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    boolean isExpired() {
        long expirationMillis = MeshHandler.DEVICE_EXPIRATION * 1000L;
        return System.currentTimeMillis() - lastSeen > expirationMillis;
    }
/*
    HashSet<Route> getRoutes() {
        return routes;
    }

    final boolean isReachable() {
        return routes.size() > 0;
    }

    public final boolean isNeighbour() {
        for (Route route : routes) {
            if (route.isDirect()) {
                return true;
            }
        }
        return false;
    }

    void mergeRoutes(RemoteDevice remoteDevice) {
        routes.addAll(remoteDevice.getRoutes());
    }
*/
    public final float getEq() {
        return ((float) echoSlidingWindow.size()) / MeshHandler.SLIDING_WINDOW_SIZE;
    }

    public final float getRq() {
        return ((float) receiveSlidingWindow.size()) / MeshHandler.SLIDING_WINDOW_SIZE;
    }

    public final float getTq() {
        float tolerance = ((float) Math.round(getEq() - 1f / MeshHandler.SLIDING_WINDOW_SIZE * 100) / 100);
        if (tolerance > getRq()) {
            System.out.println("CALCULATE TQ: EQ (" + getEq() + ") SHOULD NEVER BE GREATER THAN RQ (" + getRq() + ")");
        }
        return Math.min(getEq() / getRq(), 1);
    }

    MeshDevice getMeshDevice() {
        meshDevice.setQuality(getTq());
        meshDevice.setLastSeen(getLastSeen());
        return meshDevice;
    }

    public abstract boolean sendMessage(Message message);

    public abstract boolean remove();

    @Override
    public int compareTo(IRemoteDevice other) {
        return Float.compare(
                this.getTq(),
                other.getTq());
    }

    @Override
    public final boolean equals(Object object) {
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
