package C5_Building_Blocks.Ch5_12_Using_FutureTask_to_preload_data_that_is_needed_later;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C5_Building_Blocks.Ch5_12_Using_FutureTask_to_preload_data_that_is_needed_later.PreloaderTest.Preloader.ProductInfo;
import C5_Building_Blocks.Ch5_13_Coercing_an_unchecked_Throwable_to_a_RuntimeException.LaunderThrowable;
import support.TimeUtil;
import support.sleep.Sleep;

public class PreloaderTest {

    private final static Queue< Integer > callOrder = new ConcurrentLinkedQueue< Integer >();

    @Test
    public void test() throws InterruptedException, DataLoadException {
        Preloader preloader = new Preloader();
        preloader.start();
        Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS );
        ProductInfo productInfo = preloader.get();

        preloader.join();

        assertNotNull( productInfo );
        // 检测调用顺序
        assertEquals( new Integer( 1 ), callOrder.poll() ); // 1 - start
        assertEquals( new Integer( 2 ), callOrder.poll() ); // 2 -
                                                            // loadProductInfo
        assertEquals( new Integer( 3 ), callOrder.poll() ); // 3 - get
    }

    static class Preloader {
        ProductInfo loadProductInfo() throws DataLoadException {
            callOrder.add( 2 );
            System.out.println( TimeUtil.defaultNow() + " call loadProductInfo" );
            Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
            return new ProductInfo() {
            };
        }

        private final FutureTask< ProductInfo > future = new FutureTask< ProductInfo >( new Callable< ProductInfo >() {
            @Override
            public ProductInfo call() throws DataLoadException {
                return loadProductInfo();
            }
        } );

        private final Thread thread = new Thread( future );

        public void start() {
            callOrder.add( 1 );
            System.out.println( TimeUtil.defaultNow() + " call start" );
            thread.start();
        }

        public void join() throws InterruptedException {
            thread.join();
        }

        public ProductInfo get() throws DataLoadException, InterruptedException {
            try {
                callOrder.add( 3 );
                System.out.println( TimeUtil.defaultNow() + " call get" );
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

    static class DataLoadException extends Exception {
    }
}
