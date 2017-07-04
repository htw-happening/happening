package blue.happening.mesh.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import blue.happening.mesh.Message;

class StatsHolder {
    static int STATS_WINDOW_IN_SECS = 10;
    private List<StatsItem> stats;
    private double totalMessageCount;
    private double totalMessageSize;

    StatsHolder() {
        stats = new ArrayList<>();
        totalMessageCount = 0;
        totalMessageSize = 0;
    }

    StatsItem getStatItemForTs(double ts) {
        StatsItem existingItem = getExistingStatItemForTs(ts);
        StatsItem statsItem;
        if (existingItem == null) {
            statsItem = new StatsItem();
        } else {
            statsItem = existingItem;
        }
        return statsItem;
    }

    List<StatsItem> getStatsFromNow() {
        double currentTimeSecs = Math.round(System.currentTimeMillis() / 1000);
        List<StatsItem> results = new ArrayList<>();
        for (; currentTimeSecs > currentTimeSecs - STATS_WINDOW_IN_SECS; currentTimeSecs--) {
            StatsItem statsItem = getStatItemForTs(currentTimeSecs * 1000);
            results.add(statsItem);
        }
        Collections.reverse(results);
        return results;
    }

    double getTotalMessageCount() {
        return totalMessageCount;
    }

    double getTotalMessageSize() {
        return totalMessageSize;
    }

    void addMessage(Message message) {
        double currentTime = System.currentTimeMillis();

        StatsItem existingItem = getStatItemForTs(currentTime);
        StatsItem statsItem;
        if (existingItem == null) {
            statsItem = new StatsItem();
            stats.add(statsItem);
        } else {
            statsItem = existingItem;
        }

        statsItem.incrementMessageCount();
        statsItem.incrementMessageSize(message.toBytes());

        totalMessageCount++;
        totalMessageSize += message.toBytes().length;

        cleanUp();
    }

    private List<StatsItem> getExpiredStats() {
        List<StatsItem> expiredStats = new ArrayList<>();
        for (StatsItem statsItem : stats) {
            if (statsItem.isExpired()) {
                expiredStats.add(statsItem);
            } else {
                break;
            }
        }
        return expiredStats;
    }

    private void cleanUp() {
        List<StatsItem> expiredStats = getExpiredStats();
        for (StatsItem expiredStat : expiredStats) {
            stats.remove(expiredStat);
        }
    }

    private StatsItem getExistingStatItemForTs(double ts) {
        StatsItem statsItem = new StatsItem();
        statsItem.setTs(ts);

        boolean existingItemForTs = stats.contains(statsItem);
        if (existingItemForTs) {
            return stats.get(stats.indexOf(statsItem));
        } else {
            return null;
        }
    }

}
