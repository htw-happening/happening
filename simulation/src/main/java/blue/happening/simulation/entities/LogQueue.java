package blue.happening.simulation.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class LogQueue extends HashMap<UUID, LogItem> {
    private int capacity;

    LogQueue(int capacity) {
        this.capacity = capacity;
    }

    public List<LogItem> getLogs() {
        try {
            List<LogItem> values = new ArrayList<>(values());
            Collections.sort(values);
            return values;
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public LogItem put(LogItem item) {
        if (size() > capacity - 1) {
            remove(get(0));
        }
        return super.put(item.getId(), item);
    }
}
