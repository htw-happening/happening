package blue.happening.simulation.entities;

import java.util.concurrent.ArrayBlockingQueue;

import blue.happening.mesh.Message;


class LogQueue extends ArrayBlockingQueue<LogQueue.LogItem> {

    public LogQueue(int capacity) {
        super(capacity);
    }

    public boolean push(Message message, int status) {
        if (remainingCapacity() == 0) {
            poll();
        }
        return offer(new LogItem(message, status));
    }

    class LogItem {
        private Message message;
        private int status;

        LogItem(Message message, int status) {
            this.message = message;
            this.status = status;
        }

        public Message getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }
    }
}
