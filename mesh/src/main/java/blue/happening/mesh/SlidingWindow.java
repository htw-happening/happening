package blue.happening.mesh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class SlidingWindow extends HashSet<Integer> {

    private int sequence;

    SlidingWindow() {
        sequence = ThreadLocalRandom.current().nextInt(MeshHandler.INITIAL_MIN_SEQUENCE, MeshHandler.INITIAL_MAX_SEQUENCE);
    }

    void addIfIsSequenceInWindow(Message message) {
        if (isSequenceInWindow(message.getSequence())) {
            System.out.println("ADD MESSAGE #" + message.getSequence() + " FROM " + message.getSource());
            add(message.getSequence());
        } else {
            System.out.println("DROP MESSAGE #" + message.getSequence() + " FROM " + message.getSource());
        }
    }

    void slideSequence(int sequence) {
        if (isSequenceOutOfWindow(sequence)) {
            this.sequence = sequence;
            for (int outdatedSequence : getOutdatedSequences()) {
                remove(outdatedSequence);
            }
        } else {
            System.out.println("This shouldn't happen");
        }
    }

    private List<Integer> getOutdatedSequences() {
        List<Integer> outdatedSequences = new ArrayList<>();
        for (Integer sequence : this) {
            if (isSequenceOutOfWindow(sequence)) {
                outdatedSequences.add(sequence);
            }
        }
        return outdatedSequences;
    }

    boolean isSequenceOutOfWindow(int sequence) {
        return (sequence > this.sequence) || (sequence < this.sequence - MeshHandler.SLIDING_WINDOW_SIZE);
    }

    boolean isSequenceInWindow(int sequence) {
        return !isSequenceOutOfWindow(sequence);
    }
}
