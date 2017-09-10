package C5_Building_Blocks.Ch5_11_Using_CountDownLatch_for_starting_and_stopping_threads_in_timing_tests;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.sleep.Sleep;

public class TestHarnessTest {

    @Test
    public void test() throws InterruptedException {
        TestHarness tester = new TestHarness();

        long nanoSpan = tester.timeTasks( Runtime.getRuntime().availableProcessors(), new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Sleep.sleepUninterruptibly( 1, TimeUnit.MILLISECONDS );
                }
            }

        } );

        System.out.println( nanoSpan );
    }
}
