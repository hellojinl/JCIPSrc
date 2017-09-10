package C4_composing_objects.C4_5_Mutable_point_class_similar_to_java_awt_Point;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import testUtils.ConcurrentTestExecutor;

public class MutablePointTest {

    final ExecutorService pool = Executors.newCachedThreadPool();
    final int threadsCount = Runtime.getRuntime().availableProcessors() + 1;
    final int executionTimesPerThread = 1000;
    final CyclicBarrier barrier = new CyclicBarrier( threadsCount + 1 );
    final int expected = threadsCount * executionTimesPerThread;

    @Test
    public void test() {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {

            final MutablePoint point = new MutablePoint();

            try {
                for (int i = 0; i < threadsCount; i++) {
                    pool.execute( new Runnable() {

                        @Override
                        public void run() {
                            try {
                                barrier.await();

                                for (int i = 0; i < executionTimesPerThread; i++) {
                                    point.x++;
                                    point.y++;
                                }

                                barrier.await();
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();
                            } catch ( BrokenBarrierException e ) {
                                e.printStackTrace();
                            }
                        }

                    } );
                }
                barrier.await(); // 所有线程同时开始
                barrier.await(); // 所有线程同时结束

                System.out.println( "expected = " + expected + ", point.x = " + point.x + ", point.y = " + point.y );

                if (expected != point.x) {
                    return true;
                } else if (expected != point.y) {
                    return true;
                } else {
                    return false;
                }

            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        } ) );
    }
}
