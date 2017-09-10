package Ch14_Building_Custom_Synchronizers.C14_9_Recloseable_gate_using_wait_and_notifyAll;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * ThreadGate
 * <p/>
 * Recloseable gate using wait and notifyAll
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ThreadGate {
    // CONDITION-PREDICATE: opened-since(n) (isOpen || generation>n)
    @GuardedBy("this")
    private boolean isOpen;
    @GuardedBy("this")
    private int generation;

    public synchronized void close() {
        isOpen = false;
    }

    public synchronized void open() {
        ++generation;
        isOpen = true;
        notifyAll();
    }

    // BLOCKS-UNTIL: opened-since(generation on entry)
    public synchronized void await() throws InterruptedException {
        int arrivalGeneration = generation;
        while ( !isOpen && arrivalGeneration == generation )
            wait();
    }
}
