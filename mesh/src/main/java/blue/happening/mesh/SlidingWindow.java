package blue.happening.mesh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class SlidingWindow extends HashSet<Integer> {

    private Integer sequence;

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
        if (this.sequence == null) {
            return true;
        } else {
            return (sequence > this.sequence) || (sequence <= this.sequence - MeshHandler.SLIDING_WINDOW_SIZE);
        }
    }

    boolean isSequenceInWindow(int sequence) {
        return !isSequenceOutOfWindow(sequence);
    }
}
