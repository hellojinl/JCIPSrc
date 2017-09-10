package C7_Cancellation_and_Shutdown.C7_7_Noncancelable_task_that_restores_interruption_before_exit;

import java.util.concurrent.BlockingQueue;

/**
 * NoncancelableTask
 * <p/>
 * Noncancelable task that restores interruption before exit
 *
 * @author Brian Goetz and Tim Peierls
 */
public class NoncancelableTask {
    public Task getNextTask(BlockingQueue< Task > queue) {
        boolean interrupted = false;
        try {
            while ( true ) {
                try {
                    return queue.take();
                } catch ( InterruptedException e ) {
                    interrupted = true;
                    // fall through and retry
                }
            }
        } finally {
            if (interrupted)
                Thread.currentThread().interrupt();
        }
    }

    interface Task {
    }
}
