package blue.happening.simulation.entities;

import java.util.UUID;

import blue.happening.mesh.Message;

public class LogItem implements Comparable<LogItem> {
    private Message message;
    private int status;
    private UUID id;
    private long ts;

    LogItem(Message message, int status, UUID id) {
        this.message = message;
        this.status = status;
        this.id = id;
        this.ts = System.currentTimeMillis();
    }

    public Message getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public long getTs() {
        return ts;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (!(object instanceof LogItem)) {
            return false;
        }
        return ((LogItem) object).getId() == getId();
    }

    @Override
    public int compareTo(LogItem logItem) {
        return Long.compare(logItem.getTs(), getTs());
    }
}
