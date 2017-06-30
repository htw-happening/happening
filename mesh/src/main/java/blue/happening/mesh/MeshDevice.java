package blue.happening.mesh;

public class MeshDevice {

    private float quality;
    private String uuid;
    private int receivedSize;
    private int sentSize;
    private long lastSeen;

    public float getQuality() {
        return quality;
    }

    void setQuality(float quality) {
        this.quality = quality;
    }

    public String getUuid() {
        return uuid;
    }

    void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getReceivedSize() {
        return receivedSize;
    }

    void setReceivedSize(int receivedSize) {
        this.receivedSize = receivedSize;
    }

    public int getSentSize() {
        return sentSize;
    }

    void setSentSize(int sentSize) {
        this.sentSize = sentSize;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (!(object instanceof MeshDevice)) {
            return false;
        }
        return ((MeshDevice) object).getUuid().equals(getUuid());
    }
}
