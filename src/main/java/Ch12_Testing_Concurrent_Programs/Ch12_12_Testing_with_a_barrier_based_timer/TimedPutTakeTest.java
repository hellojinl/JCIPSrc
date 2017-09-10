package Ch12_Testing_Concurrent_Programs.Ch12_12_Testing_with_a_barrier_based_timer;

import java.util.concurrent.CyclicBarrier;

import Ch12_Testing_Concurrent_Programs.Ch12_11_Barrier_based_timer.BarrierTimer;

/**
 * TimedPutTakeTest
 * <p/>
 * Testing with a barrier-based timer
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TimedPutTakeTest extends PutTakeTest {
    private BarrierTimer timer = new BarrierTimer();

    public TimedPutTakeTest(int cap, int pairs, int trials) {
        super( cap, pairs, trials );
        barrier = new CyclicBarrier( nPairs * 2 + 1, timer );
    }

    public void test() {
        try {
            timer.clear();
            for (int i = 0; i < nPairs; i++) {
                pool.execute( new PutTakeTest.Producer() );
                pool.execute( new PutTakeTest.Consumer() );
            }
            barrier.await();
            barrier.await();
            long nsPerItem = timer.getTime() / (nPairs * (long) nTrials);
            System.out.print( "Throughput: " + nsPerItem + " ns/item" );
            assertEquals( putSum.get(), takeSum.get() );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private void assertEquals(int i, int j) {
        if (i != j) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) throws Exception {
        int tpt = 100000; // trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10) {
            System.out.println( "Capacity: " + cap );
            for (int pairs = 1; pairs <= 128; pairs *= 2) {
                TimedPutTakeTest t = new TimedPutTakeTest( cap, pairs, tpt );
                System.out.print( "Pairs: " + pairs + "\t" );
                t.test();
                System.out.print( "\t" );
                Thread.sleep( 1000 );
                t.test();
                System.out.println();
                Thread.sleep( 1000 );
            }
        }
        PutTakeTest.pool.shutdown();
    }
}
