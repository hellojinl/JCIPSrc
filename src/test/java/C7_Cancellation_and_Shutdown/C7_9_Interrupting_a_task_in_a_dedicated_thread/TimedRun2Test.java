package C7_Cancellation_and_Shutdown.C7_9_Interrupting_a_task_in_a_dedicated_thread;

import static org.junit.Assert.fail;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C5_Building_Blocks.Ch5_13_Coercing_an_unchecked_Throwable_to_a_RuntimeException.LaunderThrowable;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TimedRun2Test {

    /**
     * 如果r.run()是一个不响应中断的方法，那么就可以忽略cancelExec的影响，那么问题就集中到join方法上，
     * 当join方法返回的时候有两种可能，一种是r.run()正常结束，另一种是r.run()运行超时且此时还没有异常抛出（不代表将来没有），
     * 那么这两种情况将无法区分，你很可能认为它运行成功了。
     * 
     * 这里将用代码展示第二种情况
     * 
     */
    @Test
    public void test() throws InterruptedException {
        TimedRun2.timedRun( new NonInterruptibleRunnable(), 1, TimeUnit.MILLISECONDS ); // feel
                                                                                        // uncertain
        System.out.println( "你可能认为run运行成功了，因为到这一步它还没来得及抛出异常看起来和正常结束一样，但是请等一下，让我们增加timeout时间之后再看看" );
        try {
            TimedRun2.timedRun( new NonInterruptibleRunnable(), 200, TimeUnit.MILLISECONDS ); // throw
                                                                                              // a
                                                                                              // RuntimeException
            fail( "应该抛出IllegalStateException" );
        } catch ( RuntimeException ex ) {
            System.out.println( "在增加timeout时间之后你才会发现，run方法其实会抛出了一个异常" );
            ex.printStackTrace();
        }

        // 所以在timeout时间不够长的情况下，你是无法判断程序是正常结束，还是由于join运行超时才结束的

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

    /**
     * It's not good enough (-_-)
     */
    static class TimedRun2 {
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
            final Thread taskThread = new Thread( task ); // ！！和TimedRun1的关键区别在这条语句，这里新建了一个线程（你能完全控制它），而不是使用调用线程Thread.currentThread()
            taskThread.start();
            cancelExec.schedule( new Runnable() {
                public void run() {
                    taskThread.interrupt();
                }
            }, timeout, unit );
            taskThread.join( unit.toMillis( timeout ) ); // ！！
                                                         // 这条语句将使你产生困惑，正常结束？超时？谁知道呢
            task.rethrow();
        }
    }
}
