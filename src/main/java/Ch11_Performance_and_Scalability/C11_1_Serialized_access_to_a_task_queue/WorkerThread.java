package Ch11_Performance_and_Scalability.C11_1_Serialized_access_to_a_task_queue;

import java.util.concurrent.BlockingQueue;

/**
 * WorkerThread
 * <p/>
 * Serialized access to a task queue
 *
 * @author Brian Goetz and Tim Peierls
 */

public class WorkerThread extends Thread {
    private final BlockingQueue< Runnable > queue;

    public WorkerThread(BlockingQueue< Runnable > queue) {
        this.queue = queue;
    }

    public void run() {
        while ( true ) {
            try {
                Runnable task = queue.take();
                task.run();
            } catch ( InterruptedException e ) {
                break; /* Allow thread to exit */
            }
        }
    }
}
