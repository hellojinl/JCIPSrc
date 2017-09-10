package C8_Applying_Thread_Pools.C8_9_Thread_pool_extended_with_logging_and_timing;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.junit.Test;

import support.log.MyLogManager;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TimingThreadPoolTest {

    @Test
    public void test() throws InterruptedException {
        TimingThreadPool pool = new TimingThreadPool();
        pool.execute( new Runnable() {

            @Override
            public void run() {
                System.out.println( "exec run" );
            }

        } );
        pool.shutdown();
        pool.awaitTermination( 5, TimeUnit.SECONDS );
    }

    /**
     * 计时线程池
     *
     */
    public class TimingThreadPool extends ThreadPoolExecutor {

        public TimingThreadPool() {
            super( 1, 1, 0L, TimeUnit.SECONDS, new SynchronousQueue< Runnable >() );
        }

        private final ThreadLocal< Long > startTime = new ThreadLocal< Long >();
        private final Logger log = MyLogManager.getLogger( "TimingThreadPool" );
        private final AtomicLong numTasks = new AtomicLong();
        private final AtomicLong totalTime = new AtomicLong();

        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute( t, r );
            log.fine( String.format( "Thread %s: start %s", t, r ) );
            startTime.set( System.nanoTime() );
        }

        protected void afterExecute(Runnable r, Throwable t) {
            try {
                long endTime = System.nanoTime();
                long taskTime = endTime - startTime.get();
                numTasks.incrementAndGet();
                totalTime.addAndGet( taskTime );
                log.fine( String.format( "Thread %s: end %s, time=%dns", t, r, taskTime ) );
            } finally {
                super.afterExecute( r, t );
            }
        }

        protected void terminated() {
            try {
                log.info( String.format( "Terminated: avg time=%dns", totalTime.get() / numTasks.get() ) );
            } finally {
                super.terminated();
            }
        }
    }

}
