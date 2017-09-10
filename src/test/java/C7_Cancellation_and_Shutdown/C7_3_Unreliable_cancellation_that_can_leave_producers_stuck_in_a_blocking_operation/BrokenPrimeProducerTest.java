package C7_Cancellation_and_Shutdown.C7_3_Unreliable_cancellation_that_can_leave_producers_stuck_in_a_blocking_operation;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.TimeUtil;
import support.sleep.Sleep;

/**
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class BrokenPrimeProducerTest {

    @Test
    public void justRunIt() throws InterruptedException {
        BlockingQueue< BigInteger > queue = new ArrayBlockingQueue<>( 1 ); // 容量为1个元素，让其put的时候阻塞
        BrokenPrimeProducer producer = new BrokenPrimeProducer( queue );
        producer.start();

        while ( queue.size() < 1 ) {
            Sleep.sleepUninterruptibly( 10, TimeUnit.MILLISECONDS );
        }
        producer.cancel();

        System.out.println( TimeUtil.defaultNow() + " wait 5 seconds" );
        Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS );
        queue.take(); // 取出一个元素，使cancelled的状态由机会被检查到

        producer.join();
    }

    class BrokenPrimeProducer extends Thread {
        private final BlockingQueue< BigInteger > queue;
        private volatile boolean cancelled = false;

        BrokenPrimeProducer(BlockingQueue< BigInteger > queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while ( !cancelled ) {
                    System.out.println( TimeUtil.defaultNow() + " put a prime start" );
                    queue.put( p = p.nextProbablePrime() );
                    System.out.println( TimeUtil.defaultNow() + " put a prime finished" );
                }
                System.out.println( TimeUtil.defaultNow() + " cancelled" );
            } catch ( InterruptedException consumed ) {
            }
        }

        public void cancel() {
            cancelled = true;
            System.out.println( TimeUtil.defaultNow() + " try to cancel" );
        }
    }

}
