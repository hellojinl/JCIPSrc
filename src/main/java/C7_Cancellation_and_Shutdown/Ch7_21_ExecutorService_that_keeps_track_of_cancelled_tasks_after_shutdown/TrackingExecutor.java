package C7_Cancellation_and_Shutdown.Ch7_21_ExecutorService_that_keeps_track_of_cancelled_tasks_after_shutdown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TrackingExecutor
 * <p/>
 * ExecutorService that keeps track of cancelled tasks after shutdown
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set< Runnable > tasksCancelledAtShutdown = Collections.synchronizedSet( new HashSet< Runnable >() );

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    public void shutdown() {
        exec.shutdown();
    }

    public List< Runnable > shutdownNow() {
        return exec.shutdownNow();
    }

    public boolean isShutdown() {
        return exec.isShutdown();
    }

    public boolean isTerminated() {
        return exec.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination( timeout, unit );
    }

    public List< Runnable > getCancelledTasks() {
        if (!exec.isTerminated())
            throw new IllegalStateException( /* ... */ );
        return new ArrayList< Runnable >( tasksCancelledAtShutdown );
    }

    public void execute(final Runnable runnable) {
        exec.execute( new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } finally {
                    if (isShutdown() && Thread.currentThread().isInterrupted())
                        tasksCancelledAtShutdown.add( runnable ); // 收集关闭后取消的任务（即已经开始还没有正常完成的任务）
                }
            }
        } );
    }
}
