package C7_Cancellation_and_Shutdown.Ch7_21_ExecutorService_that_keeps_track_of_cancelled_tasks_after_shutdown;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TrackingExecutorTest {

    @Test
    public void test() throws InterruptedException {
        TrackingExecutor executor = new TrackingExecutor( Executors.newSingleThreadExecutor() );
        executor.execute( new AlwaysRun() );

        executor.execute( new AwaitingTask() );

        List< Runnable > awaitingTasks = executor.shutdownNow(); // execute内部创建的匿名Runnable
        assertEquals( 1, awaitingTasks.size() );

        executor.awaitTermination( 2, TimeUnit.SECONDS );

        List< Runnable > cancelledTasks = executor.getCancelledTasks(); // 这里提交的Runnable
        assertEquals( 1, cancelledTasks.size() );
        System.out.println( "被取消的任务是：" + ((Namable) cancelledTasks.get( 0 )).name() );
    }

    interface Namable {

        String name();

    }

    class AlwaysRun implements Runnable, Namable {

        @Override
        public void run() {
            System.out.println( name() + "： start Runnable" );

            try {
                while ( true )
                    TimeUnit.SECONDS.sleep( 10 ); // 一个不会自己结束的方法，所以shutdown()方法无法结束它，必须调用shutdownNow()
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt(); // 恢复中断状态

                // 注意这里的run在使用的时候只是一个方法调用runnable.run()
                // 所以这里的当前线程和外层调用的线程是同一个
                // 所以设置完interrupt之后，外层可以通过Thread.currentThread().isInterrupted()查看中断状态
                // 但是，假如这里的run没有外层调用，直接作为线程运行，那么这里的Thread.currentThread().interrupt()将没有意义
                // 因为线程会结束，结束之后，isInterrupted永远是false；
            }

        }

        @Override
        public String name() {
            return "AlwaysRun";
        }

    }

    class AwaitingTask implements Runnable, Namable {

        @Override
        public void run() {
            System.out.println( "shouldn't run" );
        }

        @Override
        public String name() {
            return "AwaitingTask";
        }

    }
}
