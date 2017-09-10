package Ch12_Testing_Concurrent_Programs.C12_1_Bounded_buffer_using_Semaphore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SemaphoreBoundedBufferTest {

    private static final long LOCKUP_DETECT_TIMEOUT = 1000;
    private static final int CAPACITY = 10000;
    private static final int THRESHOLD = 10000;

    @Test
    public void testIsEmptyWhenConstructed() {
        SemaphoreBoundedBuffer< Integer > bb = new SemaphoreBoundedBuffer< Integer >( 10 );
        assertTrue( bb.isEmpty() );
        assertFalse( bb.isFull() );
    }

    @Test
    public void testIsFullAfterPuts() throws InterruptedException {
        SemaphoreBoundedBuffer< Integer > bb = new SemaphoreBoundedBuffer< Integer >( 10 );
        for (int i = 0; i < 10; i++)
            bb.put( i );
        assertTrue( bb.isFull() );
        assertFalse( bb.isEmpty() );
    }

    @Test
    public void testTakeBlocksWhenEmpty() {
        final SemaphoreBoundedBuffer< Integer > bb = new SemaphoreBoundedBuffer< Integer >( 10 );
        Thread taker = new Thread() {
            public void run() {
                try {
                    int unused = bb.take();
                    fail( "if we get here, it's an error" ); // if we get here,
                                                             // it's an error
                } catch ( InterruptedException success ) {
                }
            }
        };
        try {
            taker.start();
            Thread.sleep( LOCKUP_DETECT_TIMEOUT );
            taker.interrupt();
            taker.join( LOCKUP_DETECT_TIMEOUT );
            assertFalse( taker.isAlive() );
        } catch ( Exception unexpected ) {
            fail( "unexpected" );
        }
    }

    class Big {
        double[] data = new double[ 100000 ];
    }

    // 跳过，需要设置合适的堆大小
    // @Test
    public void testLeak() throws InterruptedException {
        SemaphoreBoundedBuffer< Big > bb = new SemaphoreBoundedBuffer< Big >( CAPACITY );
        int heapSize1 = snapshotHeap();
        for (int i = 0; i < CAPACITY; i++)
            bb.put( new Big() );
        for (int i = 0; i < CAPACITY; i++)
            bb.take();
        int heapSize2 = snapshotHeap();
        assertTrue( Math.abs( heapSize1 - heapSize2 ) < THRESHOLD );
    }

    private int snapshotHeap() {
        /* Snapshot heap and return heap size */

        return 0;
    }

}
