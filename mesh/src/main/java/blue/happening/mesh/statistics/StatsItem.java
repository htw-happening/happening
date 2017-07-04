package blue.happening.mesh.statistics;

public class StatsItem {

    private double ts;
    private int messageCount;
    private int messageSize;

    StatsItem() {
        messageCount = 0;
        messageSize = 0;
    }

    public double getTs() {
        return ts;
    }

    public double getTsInSeconds() {
        return Math.floor(this.getTs() / 1000);
    }

    public void setTs(double ts) {
        this.ts = ts;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void incrementMessageCount() {
        messageCount++;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public void incrementMessageSize(byte[] bytes) {
        messageSize += bytes.length;
    }

    public boolean isExpired() {
        double currentTime = System.currentTimeMillis();
        double expirationMillis = StatsHolder.STATS_WINDOW_IN_SECS * 1000;
        if (currentTime - this.getTs() > expirationMillis) {
            return true;
        } else {
            return false;
        }
    }

    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (!(object instanceof StatsItem)) {
            return false;
        }
        return ((StatsItem) object).getTsInSeconds() == getTsInSeconds();
    }
}
