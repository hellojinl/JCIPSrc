package Ch13_Explicit_Locks.C13_4_Locking_with_a_time_budget;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TimedLocking
 * <p/>
 * Locking with a time budget
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TimedLocking {
    private Lock lock = new ReentrantLock();

    public boolean trySendOnSharedLine(String message, long timeout, TimeUnit unit) throws InterruptedException {
        long nanosToLock = unit.toNanos( timeout ) - estimatedNanosToSend( message );
        if (!lock.tryLock( nanosToLock, NANOSECONDS ))
            return false;
        try {
            return sendOnSharedLine( message );
        } finally {
            lock.unlock();
        }
    }

    private boolean sendOnSharedLine(String message) {
        /* send something */
        return true;
    }

    long estimatedNanosToSend(String message) {
        return message.length();
    }
}
