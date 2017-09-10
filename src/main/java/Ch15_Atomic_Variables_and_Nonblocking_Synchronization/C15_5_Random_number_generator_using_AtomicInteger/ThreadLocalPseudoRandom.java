package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_5_Random_number_generator_using_AtomicInteger;

import support.PseudoRandom;

/**
 * ThreadLocalPseudoRandom
 * <p/>
 * Random number generator using ThreadLocal
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ThreadLocalPseudoRandom extends PseudoRandom {

    private final ThreadLocal< Integer > seed;

    ThreadLocalPseudoRandom(int seed) {
        this.seed = new ThreadLocal< Integer >() {
            protected Integer initialValue() {
                return seed;
            }
        };
    }

    public int nextInt(int n) {
        int s = seed.get();
        seed.set( calculateNext( s ) );
        int remainder = s % n;
        return remainder > 0 ? remainder : remainder + n;
    }
}
