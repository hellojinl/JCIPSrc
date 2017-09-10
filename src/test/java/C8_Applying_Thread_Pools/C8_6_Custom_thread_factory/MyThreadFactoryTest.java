package C8_Applying_Thread_Pools.C8_6_Custom_thread_factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C8_Applying_Thread_Pools.C8_7_Custom_thread_base_class.MyAppThread;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class MyThreadFactoryTest {

    @Test
    public void test() {
        ExecutorService exec = Executors.newCachedThreadPool( new MyThreadFactory( "MyThreadFactoryTest pool" ) );
        MyAppThread.setDebug( true );
        for (int i = 0; i < 10; i++) {
            exec.execute( new PrintAliveRunnable( i + "" ) );
        }

        Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS );
    }

    class PrintAliveRunnable implements Runnable {

        private final String name;

        PrintAliveRunnable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println( "[" + name + "] alive = " + MyAppThread.getThreadsAlive() );
        }

    }
}
