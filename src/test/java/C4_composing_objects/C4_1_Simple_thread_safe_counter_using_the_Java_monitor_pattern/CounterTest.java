package C4_composing_objects.C4_1_Simple_thread_safe_counter_using_the_Java_monitor_pattern;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

public class CounterTest {

    Counter counter;

    ExecutorService pool;
    int threadsCount;
    int executionTimesPerThread;
    CyclicBarrier barrier;

    @Before
    public void setUp() throws Exception {
        counter = new Counter();

        pool = Executors.newCachedThreadPool();
        threadsCount = Runtime.getRuntime().availableProcessors() + 1;
        executionTimesPerThread = 1000;
        barrier = new CyclicBarrier( threadsCount + 1 );
    }

    @Test
    public void test() throws InterruptedException, BrokenBarrierException {
        for (int i = 0; i < threadsCount; i++) {
            pool.execute( new Runnable() {

                @Override
                public void run() {
                    try {
                        barrier.await();
                        for (int i = 0; i < executionTimesPerThread; i++) {
                            counter.increment();
                        }
                        barrier.await();
                    } catch ( Exception e ) {
                        throw new RuntimeException( e );
                    }
                }

            } );
        }
        barrier.await();
        barrier.await();

        assertEquals( threadsCount * executionTimesPerThread, counter.getValue() );
    }
}
