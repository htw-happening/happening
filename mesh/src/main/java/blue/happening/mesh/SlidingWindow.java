package blue.happening.mesh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class SlidingWindow extends HashSet<Integer> {

    private Integer sequence;

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

    Integer getSequence() {
        return sequence;
    }

    public boolean isSequenceOutOfWindow(int sequence) {
        if (this.sequence == null) {
            return true;
        } else {
            return (sequence > this.sequence) || (sequence <= this.sequence - MeshHandler.SLIDING_WINDOW_SIZE);
        }
    }

    private boolean isSequenceInWindow(int sequence) {
        return !isSequenceOutOfWindow(sequence);
    }
}
