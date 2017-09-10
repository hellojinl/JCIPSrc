package C5_Building_Blocks.Ch5_12_Using_FutureTask_to_preload_data_that_is_needed_later;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import C5_Building_Blocks.Ch5_13_Coercing_an_unchecked_Throwable_to_a_RuntimeException.LaunderThrowable;

/**
 * Preloader
 *
 * Using FutureTask to preload data that is needed later
 *
 * @author Brian Goetz and Tim Peierls
 */

public class Preloader {
    ProductInfo loadProductInfo() throws DataLoadException {
        return null;
    }

    private final FutureTask< ProductInfo > future = new FutureTask< ProductInfo >( new Callable< ProductInfo >() {
        @Override
        public ProductInfo call() throws DataLoadException {
            return loadProductInfo();
        }
    } );

    private final Thread thread = new Thread( future );

    public void start() {
        thread.start();
    }

    public ProductInfo get() throws DataLoadException, InterruptedException {
        try {
            return future.get();
        } catch ( ExecutionException e ) {
            Throwable cause = e.getCause();
            if (cause instanceof DataLoadException)
                throw (DataLoadException) cause;
            else
                throw LaunderThrowable.launderThrowable( cause );
        }
    }

    interface ProductInfo {
    }
}

class DataLoadException extends Exception {
}
