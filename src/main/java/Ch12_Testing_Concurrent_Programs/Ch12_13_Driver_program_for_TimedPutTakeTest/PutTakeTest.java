package Ch12_Testing_Concurrent_Programs.Ch12_13_Driver_program_for_TimedPutTakeTest;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import Ch12_Testing_Concurrent_Programs.C12_1_Bounded_buffer_using_Semaphore.SemaphoreBoundedBuffer;
import junit.framework.TestCase;

/**
 * PutTakeTest
 * <p/>
 * Producer-consumer test program for BoundedBuffer
 *
 * @author Brian Goetz and Tim Peierls
 */
public class PutTakeTest {
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

    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {
        public Producer() {
        }

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
        public Consumer() {
            // TODO Auto-generated constructor stub
        }

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
