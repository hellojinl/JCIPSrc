package C7_Cancellation_and_Shutdown.C7_8_Scheduling_an_interrupt_on_a_borrowed_thread;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimedRun1Test {

    /**
     * 测试Runnable中的任务不响应中断的情况
     */
    @Test
    public void test_no_response_interrupt() {
        TimedRun1.timedRun( new Runnable() {

            @Override
            public void run() {
                final Thread taskThread = Thread.currentThread();

                System.out.println( "Thread " + taskThread.getId() + " start..." );

                long startTime = System.currentTimeMillis();
                while ( System.currentTimeMillis() - startTime < 2 ) {
                    System.out.println( "Thread " + taskThread.getId() + " run..." );
                }

                System.out.println( "Thread " + taskThread.getId() + " end..." );
            }

        }, 1, TimeUnit.MILLISECONDS );

        // 这里的run任务不响应中断情况，你可能认为影响仅仅是无法中断run而已，
        // 实则不然，注意，此时你已经把调用线程的中断状态设置成true了，
        // 因为你无法控制调用线程的中断策略（即如何处理这个中断状态），你也无法预知调用线程接下来的行为，但你却对调用线程产生了影响，这是很危险的
        System.out.println( "isInterrupted=" + Thread.currentThread().isInterrupted() );
        assertTrue( Thread.currentThread().isInterrupted() );

    }

    /**
     * 在外部线程中安排中断（不要这么做）
     *
     */
    static class TimedRun1 {
        private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool( 1 );

        public static void timedRun(Runnable r, long timeout, TimeUnit unit) {
            final Thread taskThread = Thread.currentThread();
            cancelExec.schedule( new Runnable() {
                public void run() {
                    taskThread.interrupt();
                    System.out.println( "Thread " + taskThread.getId() + " try to interrupt" );
                }
            }, timeout, unit );
            r.run();
        }
    }
}
