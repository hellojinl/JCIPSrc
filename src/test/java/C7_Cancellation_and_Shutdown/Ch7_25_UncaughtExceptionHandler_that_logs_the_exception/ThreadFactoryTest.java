package C7_Cancellation_and_Shutdown.Ch7_25_UncaughtExceptionHandler_that_logs_the_exception;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * 演示为线程池的所有线程设置一个UncaughtExceptionHandler
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ThreadFactoryTest {

    /**
     * execute提交的任务，才能将它抛出的异常交给未捕获异常处理器
     */
    @Test
    public void test_execute() throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool( new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {

                Thread t = new Thread( r );
                t.setUncaughtExceptionHandler( new UEHLogger() ); // one
                                                                  // UEHLogger a
                                                                  // Thread
                return t;
            }

        } );

        exec.execute( new Runnable() {

            @Override
            public void run() {
                int i = 1 / 0;
            }

        } );
        exec.awaitTermination( 5, TimeUnit.SECONDS );
    }

    /**
     * submit提交的任务，它抛出的异常不会交给未捕获异常处理器，而是封装成ExecutionException，在get的时候抛出
     */
    @Test
    public void test_submit() {
        ExecutorService exec = Executors.newCachedThreadPool( new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {

                Thread t = new Thread( r );
                t.setUncaughtExceptionHandler( new UEHLogger() );
                return t;
            }

        } );

        Future< Integer > f = exec.submit( new Callable< Integer >() {

            @Override
            public Integer call() throws Exception {
                return 1 / 0;
            }
        } );

        try {
            Integer result = f.get();
            System.out.println( result );
        } catch ( ExecutionException ex ) {
            Throwable thrown = ex.getCause();
            if (thrown instanceof ArithmeticException) {
                ArithmeticException ae = (ArithmeticException) thrown;
                System.out.println( "submit提交任务，异常被封装在ExecutionException中" );
                ae.printStackTrace();
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
