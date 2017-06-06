package blue.happening.mesh;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

class SlidingWindow extends HashSet<Integer> {

    public static final int WINDOW_SIZE = 12;

    private int sequence;

    void addIfIsSequenceInWindow(int sequence) {
        if (isSequenceInWindow(sequence)) {
            add(sequence);
        }
    }

    void slideSequence(int sequence) {
        if (isSequenceOutOfWindow(sequence)) {
            this.sequence = sequence;
            removeAll(getOutdatedSequences());
        } else {
            System.out.println("This shouldn't happen");
        }
    }

    private List<Integer> getOutdatedSequences() {
        return stream()
                .filter(this::isSequenceOutOfWindow)
                .collect(Collectors.toList());
    }

    boolean isSequenceOutOfWindow(int sequence) {
        return (sequence > this.sequence) || (sequence < this.sequence - WINDOW_SIZE);
    }

    boolean isSequenceInWindow(int sequence) {
        return !isSequenceOutOfWindow(sequence);
    }
}