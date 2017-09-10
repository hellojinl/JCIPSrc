package C7_Cancellation_and_Shutdown.Ch7_12_Encapsulating_nonstandard_cancellation_in_a_task_with_newTaskFor;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * SocketUsingTask
 * <p/>
 * Encapsulating nonstandard cancellation in a task with newTaskFor
 *
 * @author Brian Goetz and Tim Peierls
 */
public abstract class SocketUsingTask<T> implements CancellableTask< T > {
    @GuardedBy("this")
    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    public synchronized void cancel() {
        try {
            if (socket != null)
                socket.close();
        } catch ( IOException ignored ) {
        }
    }

    public RunnableFuture< T > newTask() {
        return new FutureTask< T >( this ) {

            @SuppressWarnings("finally")
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel( mayInterruptIfRunning );
                }
            }
        };
    }
}

interface CancellableTask<T> extends Callable< T > {
    void cancel();

    RunnableFuture< T > newTask();
}

@ThreadSafe
class CancellingExecutor extends ThreadPoolExecutor {
    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue );
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue, ThreadFactory threadFactory) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory );
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue, RejectedExecutionHandler handler) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler );
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler );
    }

    protected <T> RunnableFuture< T > newTaskFor(Callable< T > callable) {
        if (callable instanceof CancellableTask)
            return ((CancellableTask< T >) callable).newTask();
        else
            return super.newTaskFor( callable );
    }
}
