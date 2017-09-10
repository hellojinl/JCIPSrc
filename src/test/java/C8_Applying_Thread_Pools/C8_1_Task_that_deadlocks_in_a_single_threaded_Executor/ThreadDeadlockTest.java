package C8_Applying_Thread_Pools.C8_1_Task_that_deadlocks_in_a_single_threaded_Executor;

import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * 
 * 错误的使用线程池会产生死锁
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see ThreadNotDeadlockTest 当线程池里有足够的线程(2个)时，不产生死锁的情况演示
 */
public class ThreadDeadlockTest {

    /**
     * 线程池只有一个线程，
     * 这里的RenderPageTask任务需要等待两个子任务LoadFileTask完成才能完成，但是显然线程池中的线程不够（需要2个线程而实际只有1个）
     */
    @Test
    public void test() {
        ThreadDeadlock deadlock = new ThreadDeadlock();
        Future< String > f = deadlock.exec.submit( deadlock.new RenderPageTask() );
        try {
            f.get( 5, TimeUnit.SECONDS );
            fail( "this f.get should be a deadlock" );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( ExecutionException e ) {
            e.printStackTrace();
        } catch ( TimeoutException e ) {
            e.printStackTrace();
        }

    }

    public class ThreadDeadlock {
        ExecutorService exec = Executors.newSingleThreadExecutor();

        public class LoadFileTask implements Callable< String > {
            private final String fileName;

            public LoadFileTask(String fileName) {
                this.fileName = fileName;
            }

            public String call() throws Exception {
                System.out.println( "will never be called" );
                return fileName;
            }
        }

        public class RenderPageTask implements Callable< String > {
            public String call() throws Exception {
                Future< String > header, footer;
                header = exec.submit( new LoadFileTask( "header.html" ) );
                footer = exec.submit( new LoadFileTask( "footer.html" ) );
                String page = renderBody();

                System.out.println( page + " has been rendered" );
                return header.get() + page + footer.get();
            }

            private String renderBody() {
                return "body page";
            }
        }
    }
}
