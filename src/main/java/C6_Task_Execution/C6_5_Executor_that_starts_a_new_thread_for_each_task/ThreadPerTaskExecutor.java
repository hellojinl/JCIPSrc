package C6_Task_Execution.C6_5_Executor_that_starts_a_new_thread_for_each_task;

import java.util.concurrent.Executor;

/**
 * ThreadPerTaskExecutor
 * <p/>
 * Executor that starts a new thread for each task
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable r) {
        new Thread( r ).start();
    };
}
