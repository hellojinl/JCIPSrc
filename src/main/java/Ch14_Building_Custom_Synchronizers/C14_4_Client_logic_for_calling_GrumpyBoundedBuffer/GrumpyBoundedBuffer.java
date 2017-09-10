package Ch14_Building_Custom_Synchronizers.C14_4_Client_logic_for_calling_GrumpyBoundedBuffer;

import Ch14_Building_Custom_Synchronizers.C14_2_Base_class_for_bounded_buffer_implementations.BaseBoundedBuffer;
import support.annotations.ThreadSafe;

/**
 * GrumpyBoundedBuffer
 * <p/>
 * Bounded buffer that balks when preconditions are not met
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class GrumpyBoundedBuffer<V> extends BaseBoundedBuffer< V > {
    public GrumpyBoundedBuffer() {
        this( 100 );
    }

    public GrumpyBoundedBuffer(int size) {
        super( size );
    }

    public synchronized void put(V v) throws BufferFullException {
        if (isFull())
            throw new BufferFullException();
        doPut( v );
    }

    public synchronized V take() throws BufferEmptyException {
        if (isEmpty())
            throw new BufferEmptyException();
        return doTake();
    }
}

class ExampleUsage {
    private GrumpyBoundedBuffer< String > buffer;
    int SLEEP_GRANULARITY = 50;

    void useBuffer() throws InterruptedException {
        while ( true ) {
            try {
                String item = buffer.take();
                // use item
                break;
            } catch ( BufferEmptyException e ) {
                Thread.sleep( SLEEP_GRANULARITY );
            }
        }
    }
}

class BufferFullException extends RuntimeException {
}

class BufferEmptyException extends RuntimeException {
}
