package blue.happening.simulation.entities;

import java.util.ArrayList;
import java.util.List;


public class LogQueue {
    private int capacity;

    private List<LogItem> logs;

    public LogQueue(int capacity) {
        this.capacity = capacity;
        logs = new ArrayList<>();
    }

    public List<LogItem> getLogs(){
        return logs;
    }

    public boolean push(LogItem item) {
        if(this.logs.size()>capacity-1){
            logs.remove(logs.get(0));
        }
        return logs.add(item);
    }
}
