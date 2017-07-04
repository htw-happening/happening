package blue.happening.mesh.statistics;

import java.util.List;

public class StatsResult {
    private double totalMessageCount;
    private double totalMessageSize;
    private List<StatsItem> stats;

    public StatsResult(double totalMessageCount, double totalMessageSize, List<StatsItem> stats) {
        this.totalMessageCount = totalMessageCount;
        this.totalMessageSize = totalMessageSize;
        this.stats = stats;
    }

    public double getTotalMessageCount() {
        return totalMessageCount;
    }

    public double getTotalMessageSize() {
        return totalMessageSize;
    }

    public List<StatsItem> getStats() {
        return stats;
    }
}
