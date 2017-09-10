package C8_Applying_Thread_Pools.C8_4_Using_a_Semaphore_to_throttle_task_submission;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.TimeUtil;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class BoundedExecutorTest {

    private final static int BOUND = 1;

    /**
     * 每隔1秒，输出一个当前时间
     */
    @Test
    public void justRunIt() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool( BOUND );
        BoundedExecutor boundedExec = new BoundedExecutor( exec, BOUND );
        boundedExec.submitTask( new PrintNowTime() );
        boundedExec.submitTask( new PrintNowTime() );
        boundedExec.submitTask( new PrintNowTime() );
        boundedExec.submitTask( new PrintNowTime() );
        boundedExec.submitTask( new PrintNowTime() );

        Sleep.sleepUninterruptibly( 6, TimeUnit.SECONDS );
    }

    class PrintNowTime implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println( TimeUtil.defaultNow() );
                TimeUnit.SECONDS.sleep( 1 );
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }

    }
}
