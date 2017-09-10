package C1_introduction.C1_1_Non_thread_safe_sequence_generator;

import support.annotations.NotThreadSafe;

/**
 * UnsafeSequence
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class UnsafeSequence {
    private int value;

    /**
     * Returns a unique value.
     */
    public int getNext() {
        return value++;
    }
}
