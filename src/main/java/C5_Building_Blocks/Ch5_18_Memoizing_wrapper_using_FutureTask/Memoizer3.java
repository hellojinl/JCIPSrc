package C5_Building_Blocks.Ch5_18_Memoizing_wrapper_using_FutureTask;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import C5_Building_Blocks.Ch5_13_Coercing_an_unchecked_Throwable_to_a_RuntimeException.LaunderThrowable;
import support.Computable;

/**
 * Memoizer3
 * <p/>
 * Memoizing wrapper using FutureTask
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Memoizer3<A, V> implements Computable< A, V > {
    private final Map< A, Future< V > > cache = new ConcurrentHashMap< A, Future< V > >();
    private final Computable< A, V > c;

    public Memoizer3(Computable< A, V > c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        Future< V > f = cache.get( arg );
        if (f == null) {
            Callable< V > eval = new Callable< V >() {
                public V call() throws InterruptedException {
                    return c.compute( arg );
                }
            };
            FutureTask< V > ft = new FutureTask< V >( eval );
            f = ft;
            cache.put( arg, ft );
            ft.run(); // call to c.compute happens here
        }
        try {
            return f.get();
        } catch ( ExecutionException e ) {
            throw LaunderThrowable.launderThrowable( e.getCause() );
        }
    }
}
