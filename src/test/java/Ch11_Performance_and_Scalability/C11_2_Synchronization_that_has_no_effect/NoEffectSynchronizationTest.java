package Ch11_Performance_and_Scalability.C11_2_Synchronization_that_has_no_effect;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class NoEffectSynchronizationTest {

    private final static int THREAD_COUNT = 8;
    private final static int MAX_COUNT = 10000;

    final ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * 对比
     */
    @Test
    public void test() throws InterruptedException {
        final NoEffectSynchronization num = new NoEffectSynchronization();
        final AtomicInteger aNum = new AtomicInteger();

        for (int i = 0; i < THREAD_COUNT; i++) {
            exec.execute( new IncreaseRunnable( num ) );
            exec.execute( new IncreaseRunnable2( aNum ) );
        }
        exec.shutdown();
        exec.awaitTermination( 4, TimeUnit.SECONDS );

        System.out.println( String.format( "%d != %d(right)", num.get(), aNum.get() ) );
        assertTrue( num.get() != aNum.get() );
    }

    class IncreaseRunnable implements Runnable {

        final NoEffectSynchronization num;

        IncreaseRunnable(NoEffectSynchronization num) {
            this.num = num;
        }

        @Override
        public void run() {
            for (int i = 0; i < MAX_COUNT; i++) {
                num.increase();
            }
        }

    }

    class IncreaseRunnable2 implements Runnable {

        final AtomicInteger num;

        IncreaseRunnable2(AtomicInteger num) {
            this.num = num;
        }

        @Override
        public void run() {
            for (int i = 0; i < MAX_COUNT; i++) {
                num.incrementAndGet();
            }
        }

    }
}
