package C2_threadsafety.C2_2_Servlet_that_counts_requests_using_AtomicLong;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * 多线程环境下，验证CountingFactorizer的线程安全性
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CountingFactorizerTest {

    @Test
    public void test() {
        CountingFactorizer factorizer = new CountingFactorizer();
        int threadsCount = Runtime.getRuntime().availableProcessors() + 1; // 比你的cpu个数多1，让线程进行上下文切换
        int executionTimesPerThread = 1000;
        long expected = threadsCount * executionTimesPerThread; // 实际调用service的次数

        TestCountingFactorizerInMultithread t = new TestCountingFactorizerInMultithread( factorizer, threadsCount,
                executionTimesPerThread );
        long actual = t.execute(); // 实际结果

        assertEquals( expected, actual ); // 线程安全，两个值应该相等

        System.out.println( "[" + this.getClass().getSimpleName() + "] the count of threads is " + threadsCount
                + ", the execution times per thread are " + executionTimesPerThread + ", the expected value is "
                + expected + ", the actual value is " + actual );
    }

    private static class TestCountingFactorizerInMultithread {

        private static final ExecutorService pool = Executors.newCachedThreadPool();

        private final CyclicBarrier barrier;
        private final CountingFactorizer factor;
        private final int threadsCount;
        private final int executionTimesPerThread;

        TestCountingFactorizerInMultithread(CountingFactorizer factor, int threadsCount, int executionTimesPerThread) {
            this.factor = factor;
            this.threadsCount = threadsCount;
            this.barrier = new CyclicBarrier( threadsCount + 1 ); // 这里的 +
                                                                  // 1表示主线程（主线程控制其他线程同时开始，同时结束）
            this.executionTimesPerThread = executionTimesPerThread;
        }

        long execute() {
            try {
                for (int i = 0; i < threadsCount; i++) {
                    pool.execute( new Actuator( factor, executionTimesPerThread ) );
                }
                barrier.await(); // 所有线程同时开始
                barrier.await(); // 所有线程同时结束

                return factor.getCount();
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }

        class Actuator implements Runnable {

            private final CountingFactorizer factor;
            private final int count;

            Actuator(CountingFactorizer factor, int count) {
                this.factor = factor;
                this.count = count;
            }

            @Override
            public void run() {
                try {
                    barrier.await(); // 使线程同时开始计数

                    for (int i = 0; i < count; i++) {
                        factor.service( null, null );
                    }

                    barrier.await(); // 使线程同时结束
                } catch ( Exception e ) {
                    throw new RuntimeException( e );
                }

            }

        }

    }
}
