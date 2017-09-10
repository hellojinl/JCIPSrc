package Ch13_Explicit_Locks.C13_2_Guarding_object_state_using_ReentrantLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用ReentrantLock来保护对象状态
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class AtomicCounter {

    final Lock lock = new ReentrantLock();
    private int i;

    public void increase() {
        lock.lock();
        try {
            i++;
        } finally {
            lock.unlock();
        }
    }

    public int get() {
        lock.lock();
        try {
            return i;
        } finally {
            lock.unlock();
        }
    }

}
