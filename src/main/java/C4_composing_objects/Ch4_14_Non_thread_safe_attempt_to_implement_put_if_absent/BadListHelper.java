package C4_composing_objects.Ch4_14_Non_thread_safe_attempt_to_implement_put_if_absent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import support.annotations.NotThreadSafe;

/**
 * ListHelder
 * <p/>
 * Examples of non-thread-safe implementations of put-if-absent helper methods
 * for List
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class BadListHelper<E> {
    public List< E > list = Collections.synchronizedList( new ArrayList< E >() );

    public synchronized boolean putIfAbsent(E x) {
        boolean absent = !list.contains( x );
        if (absent)
            list.add( x );
        return absent;
    }
}
