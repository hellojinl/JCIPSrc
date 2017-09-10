package Ch14_Building_Custom_Synchronizers.C14_5_Bounded_buffer_using_crude_blocking;

import Ch14_Building_Custom_Synchronizers.C14_2_Base_class_for_bounded_buffer_implementations.BaseBoundedBuffer;
import support.annotations.ThreadSafe;

/**
 * SleepyBoundedBuffer
 * <p/>
 * Bounded buffer using crude blocking
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class SleepyBoundedBuffer<V> extends BaseBoundedBuffer< V > {
    int SLEEP_GRANULARITY = 60;

    public SleepyBoundedBuffer() {
        this( 100 );
    }

    public SleepyBoundedBuffer(int size) {
        super( size );
    }

    public void put(V v) throws InterruptedException {
        while ( true ) {
            synchronized ( this ) {
                if (!isFull()) {
                    doPut( v );
                    return;
                }
            }
            Thread.sleep( SLEEP_GRANULARITY );
        }
    }

    public V take() throws InterruptedException {
        while ( true ) {
            synchronized ( this ) {
                if (!isEmpty())
                    return doTake();
            }
            Thread.sleep( SLEEP_GRANULARITY );
        }
    }
}
