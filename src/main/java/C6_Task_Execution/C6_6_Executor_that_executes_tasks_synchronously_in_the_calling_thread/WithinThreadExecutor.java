package C6_Task_Execution.C6_6_Executor_that_executes_tasks_synchronously_in_the_calling_thread;

import java.util.concurrent.Executor;

/**
 * WithinThreadExecutor
 * <p/>
 * Executor that executes tasks synchronously in the calling thread
 *
 * @author Brian Goetz and Tim Peierls
 */
public class WithinThreadExecutor implements Executor {
    public void execute(Runnable r) {
        r.run();
    };
}
