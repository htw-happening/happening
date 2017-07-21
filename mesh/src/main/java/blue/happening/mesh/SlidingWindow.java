package blue.happening.mesh;

import java.util.HashSet;
import java.util.Iterator;

class SlidingWindow extends HashSet<Integer> {

    private Integer sequence;

    void slideAndAddSequence(int sequence) {
        slideSequence(sequence);
        add(sequence);
    }

    void slideSequence(int sequence) {
        if (isSequenceOutOfWindow(sequence)) {
            this.sequence = sequence;
        }
        removeOutedated();
    }

    private void removeOutedated() {
        Iterator<Integer> i = iterator();
        while (i.hasNext()) {
            Integer s = i.next();
            if (isSequenceOutOfWindow(s)) {
                i.remove();
            }
        }
    }

    boolean isSequenceOutOfWindow(int sequence) {
        if (this.sequence == null) {
            return true;
        } else {
            return (sequence > this.sequence) || (sequence <= this.sequence - MeshHandler.SLIDING_WINDOW_SIZE);
        }
    }
}
