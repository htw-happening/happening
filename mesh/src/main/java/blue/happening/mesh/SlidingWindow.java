package blue.happening.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class SlidingWindow extends HashMap<Integer, Integer> {

    public static final int WINDOW_SIZE = 12;
    public static final int ECHO_MESSAGE = 1;
    public static final int RECEIVED_MESSAGE = 2;
    private static Logger logger = new Logger();

    private int sequence;
    private String uuid;

    SlidingWindow(String uuid) {
        this.uuid = uuid;
    }

    void addIfIsSequenceInWindow(Message message) {
        int type;
        if (message.getSource().equals(uuid)) {
            type = ECHO_MESSAGE;
        } else {
            type = RECEIVED_MESSAGE;
        }
        if (isSequenceInWindow(message.getSequence())) {
            put(message.getSequence(), type);
        }
    }

    void slideSequence(int sequence) {
        if (isSequenceOutOfWindow(sequence)) {
            this.sequence = sequence;
            for (int outdatedSequence : getOutdatedSequences()) {
                remove(outdatedSequence);
            }
        } else {
            logger.debug("This shouldn't happen");
        }
    }

    private List<Integer> getOutdatedSequences() {
        List<Integer> outdatedSequences = new ArrayList<>();
        for (Integer sequence : keySet()) {
            if (isSequenceOutOfWindow(sequence)) {
                outdatedSequences.add(sequence);
            }
        }
        return outdatedSequences;
    }

    boolean isSequenceOutOfWindow(int sequence) {
        return (sequence > this.sequence) || (sequence < this.sequence - WINDOW_SIZE);
    }

    boolean isSequenceInWindow(int sequence) {
        return !isSequenceOutOfWindow(sequence);
    }

    private float getEchoQuality() {
        return (float) Collections.frequency(values(), ECHO_MESSAGE) / WINDOW_SIZE;
    }

    private float getReceiveQuality() {
        return (float) Collections.frequency(values(), RECEIVED_MESSAGE) / WINDOW_SIZE;
    }

    float getTransmissionQuality() {
        return getEchoQuality() / Math.min(1, getReceiveQuality());
    }
}
