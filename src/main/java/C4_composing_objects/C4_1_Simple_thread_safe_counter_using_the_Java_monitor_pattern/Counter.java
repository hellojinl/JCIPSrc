package C4_composing_objects.C4_1_Simple_thread_safe_counter_using_the_Java_monitor_pattern;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * Counter
 * <p/>
 * Simple thread-safe counter using the Java monitor pattern
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public final class Counter {
    @GuardedBy("this")
    private long value = 0;

    public synchronized long getValue() {
        return value;
    }

    public synchronized long increment() {
        if (value == Long.MAX_VALUE)
            throw new IllegalStateException( "counter overflow" );
        return ++value;
    }
}
