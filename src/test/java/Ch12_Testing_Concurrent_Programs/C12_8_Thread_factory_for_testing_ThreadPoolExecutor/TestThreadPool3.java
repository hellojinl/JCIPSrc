package Ch12_Testing_Concurrent_Programs.C12_8_Thread_factory_for_testing_ThreadPoolExecutor;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * 一个多步走的测试
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TestThreadPool3 {

    private final TestingThreadFactory threadFactory = new TestingThreadFactory();

    @Test
    public void testPoolExpansion() throws InterruptedException {
        int MAX_SIZE = 10;
        ExecutorService exec = Executors.newFixedThreadPool( MAX_SIZE, threadFactory );

        for (int i = 0; i < MAX_SIZE; i++)
            exec.execute( new Runnable() {
                public void run() {
                    try {
                        Thread.sleep( Long.MAX_VALUE );
                    } catch ( InterruptedException e ) {
                        Thread.currentThread().interrupt();
                    }
                }
            } );

        for (int i = 0; i < 20 && threadFactory.numCreated.get() < MAX_SIZE; i++)
            Thread.sleep( 100 );

        for (int i = 0; i < 9 * MAX_SIZE; i++) {
            exec.execute( new Runnable() {
                public void run() {
                    try {
                        Thread.sleep( Long.MAX_VALUE );
                    } catch ( InterruptedException e ) {
                        Thread.currentThread().interrupt();
                    }
                }
            } );
            assertEquals( threadFactory.numCreated.get(), MAX_SIZE );
        }

        exec.shutdownNow();
    }

    class TestingThreadFactory implements ThreadFactory {
        public final AtomicInteger numCreated = new AtomicInteger();
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            numCreated.incrementAndGet();
            return factory.newThread( r );
        }
    }
}
