package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_4_Random_number_generator_using_ReentrantLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import support.PseudoRandom;
import support.annotations.ThreadSafe;

/**
 * ReentrantLockPseudoRandom
 * <p/>
 * Random number generator using ReentrantLock
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ReentrantLockPseudoRandom extends PseudoRandom {
    private final Lock lock = new ReentrantLock( false );
    private int seed;

    ReentrantLockPseudoRandom(int seed) {
        this.seed = seed;
    }

    public int nextInt(int n) {
        lock.lock();
        try {
            int s = seed;
            seed = calculateNext( s );
            int remainder = s % n;
            return remainder > 0 ? remainder : remainder + n;
        } finally {
            lock.unlock();
        }
    }
}
