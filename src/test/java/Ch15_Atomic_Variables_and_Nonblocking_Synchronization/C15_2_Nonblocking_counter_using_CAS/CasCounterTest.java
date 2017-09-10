package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_2_Nonblocking_counter_using_CAS;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_1_Simulated_CAS_operation.SimulatedCAS;
import support.annotations.ThreadSafe;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CasCounterTest {

    private final static ExecutorService pool = Executors.newCachedThreadPool();
    private final int nThreads = 5;
    private final int nTimes = 100;
    private final CasCounter counter = new CasCounter();

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < nThreads; i++) {
            pool.execute( new CounterRunnable() );
        }
        pool.shutdown();
        pool.awaitTermination( 2, TimeUnit.SECONDS );

        assertEquals( nThreads * nTimes, counter.getValue() );
    }

    class CounterRunnable implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < nTimes; i++) {
                counter.increment();
                Thread.yield();
            }
        }

    }

    @ThreadSafe
    public class CasCounter {
        private SimulatedCAS value = new SimulatedCAS();

        public int getValue() {
            return value.get();
        }

        public int increment() {
            int v;
            do {
                v = value.get();
            } while ( v != value.compareAndSwap( v, v + 1 ) );
            return v + 1;
        }
    }
}
