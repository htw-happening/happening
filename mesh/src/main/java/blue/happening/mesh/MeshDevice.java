package blue.happening.mesh;

public class MeshDevice {

    private float quality;
    private String uuid;
    private int receivedSize;
    private int sentSize;

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
}
