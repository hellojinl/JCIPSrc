package C4_composing_objects.C4_3_Guarding_state_with_a_private_lock;

import support.annotations.GuardedBy;

/**
 * PrivateLock
 * <p/>
 * Guarding state with a private lock
 *
 * @author Brian Goetz and Tim Peierls
 */
public class PrivateLock {
    private final Object myLock = new Object();
    @GuardedBy("myLock")
    Widget widget;

    void someMethod() {
        synchronized ( myLock ) {
            // Access or modify the state of widget
        }
    }

    interface Widget {
    }
}
