package Ch14_Building_Custom_Synchronizers.Ch14_12_Counting_semaphore_implemented_using_Lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.TimeUtil;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SemaphoreOnLockTest {

    private final static ExecutorService pool = Executors.newCachedThreadPool();
    private final SemaphoreOnLock s = new SemaphoreOnLock( 1 );

    @Test
    public void test() {
        for (int i = 0; i < 5; i++) {
            pool.execute( new PrintRunnable() );
        }
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );

        s.release();
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );

        s.release();
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );

        s.release();
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );

        s.release();
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
    }

    class PrintRunnable implements Runnable {

        @Override
        public void run() {
            try {
                s.acquire();
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
            System.out.println( TimeUtil.defaultNow() );

        }

    }
}
