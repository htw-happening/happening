package blue.happening.mesh.statistics;

import blue.happening.mesh.Message;

public class Stat {
    private double ts;
    private double totalMessageCount;
    private double totalMessageSize;
    private int messageCountForTs;
    private int messageSizeForTs;

    Stat() {
        ts = System.currentTimeMillis();
        totalMessageCount = 0;
        totalMessageSize = 0;
        messageCountForTs = 0;
        messageSizeForTs = 0;
    }

    public int getMessageCountForTs() {
        return messageCountForTs;
    }

    public int getMessageSizeForTs() {
        return messageSizeForTs;
    }

    public double getTotalMessageCount() {
        return totalMessageCount;
    }

    public double getTotalMessageSize() {
        return totalMessageSize;
    }

    public double getTs() {
        return ts;
    }

    void updateTs(double ts) {
        this.ts = ts;
        messageCountForTs = 0;
        messageSizeForTs = 0;
    }

    void addMessage(Message message) {
        messageCountForTs++;
        totalMessageCount++;

        messageSizeForTs += message.toBytes().length;
        totalMessageSize += message.toBytes().length;
    }

    protected Stat copy() {
        Stat stat = new Stat();
        stat.messageSizeForTs = this.messageSizeForTs;
        stat.messageCountForTs = this.messageCountForTs;
        stat.totalMessageCount = this.totalMessageCount;
        stat.totalMessageSize = this.totalMessageSize;
        return stat;
    }
}
