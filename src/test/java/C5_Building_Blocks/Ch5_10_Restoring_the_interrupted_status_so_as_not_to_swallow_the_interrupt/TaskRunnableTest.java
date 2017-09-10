package C5_Building_Blocks.Ch5_10_Restoring_the_interrupted_status_so_as_not_to_swallow_the_interrupt;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C5_Building_Blocks.Ch5_10_Restoring_the_interrupted_status_so_as_not_to_swallow_the_interrupt.TaskRunnableTest.TaskRunnable.Task;
import support.sleep.Sleep;

public class TaskRunnableTest {

    @Test
    public void test() throws InterruptedException {
        final BlockingQueue< Task > queue = new LinkedBlockingQueue< Task >();
        TaskRunnable taskRunnable = new TaskRunnable();
        taskRunnable.queue = queue;

        final Thread t = new Thread( taskRunnable );
        t.start();

        // 设置t的中断状态
        new Thread( new Runnable() {

            @Override
            public void run() {
                Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
                if (!t.isInterrupted()) {
                    t.interrupt();
                }
            }

        } ).start();

        t.join();

        // 当线程结束之后，它的中断状态永远是false
        System.out.println( "[" + t.getId() + "] isInterrupted = " + t.isInterrupted() + ", isAlive = " + t.isAlive() );
        assertEquals( false, t.isAlive() );
        assertEquals( false, t.isInterrupted() );

    }

    static class TaskRunnable implements Runnable {
        BlockingQueue< Task > queue;

        public void run() {
            try {
                processTask( queue.take() );
            } catch ( InterruptedException e ) {
                // restore interrupted status
                Thread.currentThread().interrupt(); // 如果注释掉这行，将执行doAnythingElse();
            }

            // 一个中断点，线程可在这结束
            if (Thread.currentThread().isInterrupted()) {
                System.out
                        .println( getId() + " isInterrupted = " + Thread.currentThread().isInterrupted() + ", exit!" );
                return;
            }

            doAnythingElse();
        }

        void processTask(Task task) {
            System.out.println( getId() + " process Task..." );
        }

        void doAnythingElse() {
            System.out.println( getId() + " doAnythingElse" );
        }

        private String getId() {
            return "[" + Thread.currentThread().getId() + "]";
        }

        interface Task {
        }
    }
}
