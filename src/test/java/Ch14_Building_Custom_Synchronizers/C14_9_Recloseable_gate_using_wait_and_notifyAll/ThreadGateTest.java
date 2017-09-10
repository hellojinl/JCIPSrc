package Ch14_Building_Custom_Synchronizers.C14_9_Recloseable_gate_using_wait_and_notifyAll;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ThreadGateTest {

    private final static ExecutorService pool = Executors.newCachedThreadPool();
    private final static int nThreads = 5;
    private final static int nTimes = 100;

    private final ThreadGate gate = new ThreadGate();
    private final AtomicInteger counter = new AtomicInteger();

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < nThreads; i++) {
            pool.execute( new CounterRunnable() );
        }

        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );

        gate.open();
        gate.close();

        pool.shutdown();
        pool.awaitTermination( 100, TimeUnit.SECONDS );

        assertEquals( nThreads * nTimes, counter.get() );
    }

    class CounterRunnable implements Runnable {

        @Override
        public void run() {
            try {
                gate.await();
                println( "Thread[%2d] start", Thread.currentThread().getId() );

                for (int i = 0; i < nTimes; i++)
                    counter.incrementAndGet();

            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private void println(String format, Object... args) {
        System.out.println( String.format( format, args ) );
    }
}
