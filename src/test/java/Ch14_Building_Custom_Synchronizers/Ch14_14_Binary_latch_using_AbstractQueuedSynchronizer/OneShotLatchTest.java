package Ch14_Building_Custom_Synchronizers.Ch14_14_Binary_latch_using_AbstractQueuedSynchronizer;

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
public class OneShotLatchTest {

    OneShotLatch latch = new OneShotLatch();

    @Test
    public void test() throws InterruptedException {
        new Thread( () -> {
            try {
                System.out.println( String.format( "%s Thread[%2d] await", TimeUtil.defaultNow(),
                        Thread.currentThread().getId() ) );
                latch.await();
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }

            System.out.println(
                    String.format( "%s Thread[%2d] print", TimeUtil.defaultNow(), Thread.currentThread().getId() ) );
        } ).start();

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
        System.out.println(
                String.format( "%s Thread[%2d] signal", TimeUtil.defaultNow(), Thread.currentThread().getId() ) );
        latch.signal();

        new Thread( () -> {
            try {
                System.out.println( String.format( "%s Thread[%2d] await", TimeUtil.defaultNow(),
                        Thread.currentThread().getId() ) );
                latch.await();
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }

            System.out.println(
                    String.format( "%s Thread[%2d] print", TimeUtil.defaultNow(), Thread.currentThread().getId() ) );
        } ).start();

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
    }

}
