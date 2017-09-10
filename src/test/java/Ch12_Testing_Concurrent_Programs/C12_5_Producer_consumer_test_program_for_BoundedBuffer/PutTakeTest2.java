package Ch12_Testing_Concurrent_Programs.C12_5_Producer_consumer_test_program_for_BoundedBuffer;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import Ch12_Testing_Concurrent_Programs.C12_1_Bounded_buffer_using_Semaphore.SemaphoreBoundedBuffer;
import junit.framework.TestCase;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class PutTakeTest2 {

    @Test
    public void test() throws Exception {
        new PutTakeTest( 10, 10, 100000 ).test(); // sample parameters
        PutTakeTest.pool.shutdown();
        PutTakeTest.pool.awaitTermination( 2, TimeUnit.SECONDS );
    }

    static class PutTakeTest extends TestCase {
        protected static final ExecutorService pool = Executors.newCachedThreadPool();
        protected CyclicBarrier barrier;
        protected final SemaphoreBoundedBuffer< Integer > bb;
        protected final int nTrials, nPairs;
        protected final AtomicInteger putSum = new AtomicInteger( 0 );
        protected final AtomicInteger takeSum = new AtomicInteger( 0 );

        public PutTakeTest(int capacity, int npairs, int ntrials) {
            this.bb = new SemaphoreBoundedBuffer< Integer >( capacity );
            this.nTrials = ntrials;
            this.nPairs = npairs;
            this.barrier = new CyclicBarrier( npairs * 2 + 1 );
        }

        void test() {
            try {
                for (int i = 0; i < nPairs; i++) {
                    pool.execute( new Producer() );
                    pool.execute( new Consumer() );
                }
                barrier.await(); // wait for all threads to be ready
                barrier.await(); // wait for all threads to finish
                assertEquals( putSum.get(), takeSum.get() );
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }

        static int xorShift(int y) {
            y ^= (y << 6);
            y ^= (y >>> 21);
            y ^= (y << 7);
            return y;
        }

        class Producer implements Runnable {
            public void run() {
                try {
                    int seed = (this.hashCode() ^ (int) System.nanoTime());
                    int sum = 0;
                    barrier.await();
                    for (int i = nTrials; i > 0; --i) {
                        bb.put( seed );
                        sum += seed;
                        seed = xorShift( seed );
                    }
                    putSum.getAndAdd( sum );
                    barrier.await();
                } catch ( Exception e ) {
                    throw new RuntimeException( e );
                }
            }
        }

        class Consumer implements Runnable {
            public void run() {
                try {
                    barrier.await();
                    int sum = 0;
                    for (int i = nTrials; i > 0; --i) {
                        sum += bb.take();
                    }
                    takeSum.getAndAdd( sum );
                    barrier.await();
                } catch ( Exception e ) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

}
