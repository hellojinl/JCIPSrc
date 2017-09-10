package C7_Cancellation_and_Shutdown.C7_5_Using_interruption_for_cancellation;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
public class PrimeProducerTest {

    @Test
    public void test() throws InterruptedException {
        BlockingQueue< BigInteger > queue = new ArrayBlockingQueue<>( 1 ); // 容量为1个元素，让其put的时候阻塞
        PrimeProducer producer = new PrimeProducer( queue );
        producer.start();

        while ( queue.size() < 1 ) {
            Sleep.sleepUninterruptibly( 10, TimeUnit.MILLISECONDS );
        }
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
        producer.cancel();

        producer.join();
    }

    class PrimeProducer extends Thread {
        private final BlockingQueue< BigInteger > queue;

        PrimeProducer(BlockingQueue< BigInteger > queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while ( !Thread.currentThread().isInterrupted() ) {
                    System.out.println( TimeUtil.defaultNow() + " put a prime start" );
                    queue.put( p = p.nextProbablePrime() );
                    System.out.println( TimeUtil.defaultNow() + " put a prime finished" );
                }
            } catch ( InterruptedException consumed ) {
                /* Allow thread to exit */
            }
            System.out.println( TimeUtil.defaultNow() + " cancelled" );
        }

        public void cancel() {
            System.out.println( TimeUtil.defaultNow() + " try to cancel" );
            interrupt();
        }
    }
}
