package C7_Cancellation_and_Shutdown.Ch7_12_Encapsulating_nonstandard_cancellation_in_a_task_with_newTaskFor.other;

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

import support.TimeUtil;
import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * 原来的SocketUsingTask设计我感觉有些问题（也可能是我想的不对）：它的socket是private的，然后它又是个抽象类，这将导致子类无法访问socket，然而其具体功能又
 * 必须在子类的call()方法中实现（注意call方法中无法使用socket的），也就是说call方法只能实现和socket无关的功能
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public abstract class MySocketUsingTask<T> implements CancellableTask< T > {
    @GuardedBy("this")
    private final Socket socket;

    protected MySocketUsingTask(Socket s) {
        if (s == null) {
            throw new NullPointerException();
        }
        this.socket = s;
    }

    protected synchronized Socket getSocket() {
        return this.socket;
    }

    public synchronized void cancel() {
        try {
            if (socket != null) {
                socket.close();
                System.out.println(
                        TimeUtil.defaultNow() + " [" + Thread.currentThread().getId() + "] Socket is closed" );
            }
        } catch ( IOException ignored ) {
        }
    }

    public RunnableFuture< T > newTask() {
        return new FutureTask< T >( this ) {

            @SuppressWarnings("finally")
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    MySocketUsingTask.this.cancel();
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
class MyCancellingExecutor extends ThreadPoolExecutor {
    public MyCancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue );
    }

    public MyCancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue, ThreadFactory threadFactory) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory );
    }

    public MyCancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue, RejectedExecutionHandler handler) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler );
    }

    public MyCancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue< Runnable > workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler );
    }

    @Override
    protected <T> RunnableFuture< T > newTaskFor(Callable< T > callable) {
        if (callable instanceof CancellableTask)
            return ((CancellableTask< T >) callable).newTask();
        else
            return super.newTaskFor( callable );
    }
}
