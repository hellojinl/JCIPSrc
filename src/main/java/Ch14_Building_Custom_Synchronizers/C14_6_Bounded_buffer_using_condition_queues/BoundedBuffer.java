package Ch14_Building_Custom_Synchronizers.C14_6_Bounded_buffer_using_condition_queues;

import Ch14_Building_Custom_Synchronizers.C14_2_Base_class_for_bounded_buffer_implementations.BaseBoundedBuffer;
import support.annotations.ThreadSafe;

/**
 * BoundedBuffer
 * <p/>
 * Bounded buffer using condition queues
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class BoundedBuffer<V> extends BaseBoundedBuffer< V > {
    // CONDITION PREDICATE: not-full (!isFull())
    // CONDITION PREDICATE: not-empty (!isEmpty())
    public BoundedBuffer() {
        this( 100 );
    }

    public BoundedBuffer(int size) {
        super( size );
    }

    // BLOCKS-UNTIL: not-full
    public synchronized void put(V v) throws InterruptedException {
        while ( isFull() )
            wait();
        doPut( v );
        notifyAll();
    }

    // BLOCKS-UNTIL: not-empty
    public synchronized V take() throws InterruptedException {
        while ( isEmpty() )
            wait();
        V v = doTake();
        notifyAll();
        return v;
    }

    // BLOCKS-UNTIL: not-full
    // Alternate form of put() using conditional notification
    public synchronized void alternatePut(V v) throws InterruptedException {
        while ( isFull() )
            wait();
        boolean wasEmpty = isEmpty();
        doPut( v );
        if (wasEmpty)
            notifyAll();
    }
}
