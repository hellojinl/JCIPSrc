package Ch11_Performance_and_Scalability.C11_1_Serialized_access_to_a_task_queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class WorkerThreadTest {

    ScheduledExecutorService exec = Executors.newScheduledThreadPool( 1 );

    @Test
    public void test() throws InterruptedException {
        BlockingQueue< Runnable > queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 10; i++) {
            queue.add( new PrintRunnable( i + "" ) );
        }
        final WorkerThread t = new WorkerThread( queue );
        exec.schedule( new Runnable() {

            @Override
            public void run() {
                t.interrupt();
            }

        }, 2, TimeUnit.SECONDS );

        t.start();

        t.join();

    }

    class PrintRunnable implements Runnable {

        private final String msg;

        PrintRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep( 1 );
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
            System.out.println( msg );
        }

    }
}
