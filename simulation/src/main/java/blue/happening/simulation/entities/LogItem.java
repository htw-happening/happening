package blue.happening.simulation.entities;

import java.util.UUID;

import blue.happening.mesh.Message;

public class LogItem {
    private Message message;
    private int status;
    private UUID id;

    LogItem(Message message, int status, UUID id) {
        this.message = message;
        this.status = status;
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public UUID getId() {
        return id;
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
}
