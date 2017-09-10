package C5_Building_Blocks.Ch5_10_Restoring_the_interrupted_status_so_as_not_to_swallow_the_interrupt;

import java.util.concurrent.BlockingQueue;

/**
 * TaskRunnable
 * <p/>
 * Restoring the interrupted status so as not to swallow the interrupt
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TaskRunnable implements Runnable {
    BlockingQueue< Task > queue;

    public void run() {
        try {
            processTask( queue.take() );
        } catch ( InterruptedException e ) {
            // restore interrupted status
            Thread.currentThread().interrupt();

            // 更进一步，上面这条语句运行完之后将发生什么？
            // 1.当前线程将结束（很显然），
            // 2.当前线程的isInterrupted()永远等于false
            // （参考java.lang.Thread.isInterrupted()的注释
            // A thread interruption ignored because a thread was not alive
            // at the time of the interrupt will be reflected by this method
            // returning false.）
            // 所以，仅仅从当前的代码来看的话，
            // Thread.currentThread().interrupt(); 毫无意义
            // 因为在run内部没有根据中断状态来执行任何操作（比如：结束某个循环或在接下来的代码中根据中断状态执行不同的路径等），到了外部，线程已经结束了
            // t.interrupt()永远等于false（这里外部的t = 内部的Thread.currentThread()）
            // 在这里Thread.currentThread().interrupt()最终效果和什么都不做是一样的。
            // 但是换一种用法，Thread.currentThread().interrupt()还是能发挥作用的，见TaskRunnableTest
        }
    }

    void processTask(Task task) {
        // Handle the task
    }

    interface Task {
    }
}
