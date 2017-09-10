package C7_Cancellation_and_Shutdown.C7_9_Interrupting_a_task_in_a_dedicated_thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import C5_Building_Blocks.Ch5_13_Coercing_an_unchecked_Throwable_to_a_RuntimeException.LaunderThrowable;

/**
 * TimedRun2
 * <p/>
 * Interrupting a task in a dedicated thread
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TimedRun2 {
    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool( 1 );

    public static void timedRun(final Runnable r, long timeout, TimeUnit unit) throws InterruptedException {

        class RethrowableTask implements Runnable {
            private volatile Throwable t;

            public void run() {
                try {
                    r.run();
                } catch ( Throwable t ) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null)
                    throw LaunderThrowable.launderThrowable( t );
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread( task );
        taskThread.start();
        cancelExec.schedule( new Runnable() {
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit );
        taskThread.join( unit.toMillis( timeout ) );
        task.rethrow();
    }
}
