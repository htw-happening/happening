package blue.happening.mesh.statistics;

import blue.happening.mesh.Message;

public class NetworkStats {
    private StatsHolder in;
    private StatsHolder out;

    public NetworkStats() {
        in = new StatsHolder();
        out = new StatsHolder();
    }

    public void addInComingMessage(Message message) {
        in.addMessage(message);
    }

    public StatsResult getInComingStats() {
        return new StatsResult(in.getTotalMessageCount(), in.getTotalMessageSize(), in.getStatsFromNow());
    }

    public StatsItem getIncomingStatForTs(double ts) {
        return in.getStatItemForTs(ts);
    }

    public void addOutGoingMessage(Message message) {
        out.addMessage(message);
    }

    public StatsResult getOutGoingStats() {
        return new StatsResult(out.getTotalMessageCount(), out.getTotalMessageSize(), out.getStatsFromNow());
    }

    public StatsItem getOutgoingStatForTs(double ts) {
        return out.getStatItemForTs(ts);
    }
}
