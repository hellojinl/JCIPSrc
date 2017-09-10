package C8_Applying_Thread_Pools.C8_4_Using_a_Semaphore_to_throttle_task_submission;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import support.annotations.ThreadSafe;

/**
 * BoundedExecutor
 * <p/>
 * Using a Semaphore to throttle task submission
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore( bound );
    }

    public void submitTask(final Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute( new Runnable() {
                public void run() {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                }
            } );
        } catch ( RejectedExecutionException e ) {
            semaphore.release();
        }
    }
}
