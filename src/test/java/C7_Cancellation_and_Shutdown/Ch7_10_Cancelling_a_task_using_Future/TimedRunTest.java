package C7_Cancellation_and_Shutdown.Ch7_10_Cancelling_a_task_using_Future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import C5_Building_Blocks.Ch5_13_Coercing_an_unchecked_Throwable_to_a_RuntimeException.LaunderThrowable;

public class TimedRunTest {

    @Test
    public void test() throws InterruptedException {
        TimedRun.timedRun( new NonInterruptibleRunnable(), 10, TimeUnit.MILLISECONDS );
    }

    static class NonInterruptibleRunnable implements Runnable {

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            int i = 0;
            while ( System.currentTimeMillis() - startTime < 100 ) {
                i = 100;
            }
            i = i + 1;
            throw new RuntimeException( "某个运行时问题" );
        }
    }

    static class TimedRun {
        private static final ExecutorService taskExec = Executors.newCachedThreadPool();

        public static void timedRun(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
            Future< ? > task = taskExec.submit( r );
            try {
                task.get( timeout, unit );
            } catch ( TimeoutException e ) { // 当超时时会抛出该异常，意义很明确，这是与TimedRun2（无法区分join超时和正常结束）的主要区别
                // task will be cancelled below
                e.printStackTrace(); // 为了测试
            } catch ( ExecutionException e ) {
                // exception thrown in task; rethrow
                throw LaunderThrowable.launderThrowable( e.getCause() );
            } finally {
                // Harmless if task already completed
                task.cancel( true ); // interrupt if running
            }
        }
    }
}
