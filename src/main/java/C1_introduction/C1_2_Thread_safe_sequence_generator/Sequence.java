package C1_introduction.C1_2_Thread_safe_sequence_generator;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * Sequence
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class Sequence {
    @GuardedBy("this")
    private int nextValue;

    public synchronized int getNext() {
        return nextValue++;
    }
}
