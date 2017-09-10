package C8_Applying_Thread_Pools.C8_1_Task_that_deadlocks_in_a_single_threaded_Executor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * 当线程池里有足够的线程(2个)时，不产生死锁的情况演示
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ThreadNotDeadlockTest {

    @Test
    public void justRunIt() {
        ThreadNotDeadlock notDeadlock = new ThreadNotDeadlock();
        Future< String > f = notDeadlock.exec.submit( notDeadlock.new RenderPageTask() );
        String str;
        try {
            str = f.get( 5, TimeUnit.SECONDS );
            System.out.println( "result: " + str );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( ExecutionException e ) {
            e.printStackTrace();
        } catch ( TimeoutException e ) {
            e.printStackTrace();
        }
    }

    public class ThreadNotDeadlock {
        ExecutorService exec = Executors.newFixedThreadPool( 2 ); // !! There is
                                                                  // the
                                                                  // difference
                                                                  // from
                                                                  // ThreadDeadlock

        public class LoadFileTask implements Callable< String > {
            private final String fileName;

            public LoadFileTask(String fileName) {
                this.fileName = fileName;
            }

            public String call() throws Exception {
                System.out.println( "LoadFileTask" );
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
