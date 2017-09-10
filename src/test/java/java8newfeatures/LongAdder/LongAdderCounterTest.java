package java8newfeatures.LongAdder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import testUtils.Timer;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LongAdderCounterTest {

    final int MAX_THREAD_COUNT = 10;
    final int MAX_COUNT = 1000000;
    final ExecutorService POOL1 = Executors.newFixedThreadPool( MAX_THREAD_COUNT );
    final ExecutorService POOL2 = Executors.newFixedThreadPool( MAX_THREAD_COUNT );

    final ICounter longAdderCounter = new LongAdderCounter();
    final ICounter atomicLongCounter = new AtomicLongCounter();

    @Test
    public void compare() {
        long longAdderTimeNs = Timer.time( this::useLongAdderCounter );
        long atomicLongTimeNs = Timer.time( this::useAtomicLongCounter );

        System.out.println( String.format( "%20s=%12dns", "atomicLongCounter", atomicLongTimeNs ) );
        System.out.println( String.format( "%20s=%12dns", "longAdderCounter", longAdderTimeNs ) );
        System.out.println();

        assertEquals( 0, atomicLongCounter.get() );
        assertEquals( 0, longAdderCounter.get() );
        assertTrue( atomicLongTimeNs > longAdderTimeNs );
    }

    private void useLongAdderCounter() throws InterruptedException {
        for (int c = 0; c < MAX_THREAD_COUNT; c++) {
            POOL1.execute( () -> {
                for (int i = 0; i < MAX_COUNT; i++) {
                    longAdderCounter.increment();
                }

                for (int i = 0; i < MAX_COUNT; i++) {
                    longAdderCounter.decrement();
                }
            } );
        }
        POOL1.shutdown();
        POOL1.awaitTermination( Integer.MAX_VALUE, TimeUnit.SECONDS );
    }

    private void useAtomicLongCounter() throws InterruptedException {
        for (int c = 0; c < MAX_THREAD_COUNT; c++) {
            POOL2.execute( () -> {
                for (int i = 0; i < MAX_COUNT; i++) {
                    atomicLongCounter.increment();
                }

                for (int i = 0; i < MAX_COUNT; i++) {
                    atomicLongCounter.decrement();
                }
            } );
        }
        POOL2.shutdown();
        POOL2.awaitTermination( Integer.MAX_VALUE, TimeUnit.SECONDS );
    }
}

// 某次运行结果，longAdderCounter快很多
// atomicLongCounter=3807167151ns
// longAdderCounter= 314310242ns
