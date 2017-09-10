package C1_introduction.C1_2_Thread_safe_sequence_generator;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * 多线程环境下，测试Sequence的功能，因为它是线程安全的，所以期望值应该等于实际值
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SequenceTest {

    @Test
    public void test() {
        Sequence seq = new Sequence();
        int threadsCount = Runtime.getRuntime().availableProcessors() + 1; // 比你的cpu个数多1，让线程进行上下文切换
        int executionTimesPerThread = 1000;
        int expected = threadsCount * executionTimesPerThread; // 你想要的结果

        TestSafeSequenceInMultithread t = new TestSafeSequenceInMultithread( seq, threadsCount,
                executionTimesPerThread );
        int actual = t.execute(); // 实际结果

        assertEquals( expected, actual ); // 线程安全，两个值应该相等

        System.out.println( "[" + this.getClass().getSimpleName() + "] the count of threads is " + threadsCount
                + ", the execution times per thread are " + executionTimesPerThread + ", the expected value is "
                + expected + ", the actual value is " + actual );
    }

    private static class TestSafeSequenceInMultithread {

        private static final ExecutorService pool = Executors.newCachedThreadPool();

        private final CyclicBarrier barrier;
        private final Sequence seq;
        private final int threadsCount;
        private final int executionTimesPerThread;

        TestSafeSequenceInMultithread(Sequence seq, int threadsCount, int executionTimesPerThread) {
            this.seq = seq;
            this.threadsCount = threadsCount;
            this.barrier = new CyclicBarrier( threadsCount + 1 ); // 这里的 +1
                                                                  // 表示主线程（主线程控制其他线程同时开始，同时结束）
            this.executionTimesPerThread = executionTimesPerThread;
        }

        int execute() {
            try {
                for (int i = 0; i < threadsCount; i++) {
                    pool.execute( new Counter( seq, executionTimesPerThread ) );
                }
                barrier.await(); // 所有线程同时开始
                barrier.await(); // 所有线程同时结束

                return seq.getNext();
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }

        class Counter implements Runnable {

            private final Sequence seq;
            private final int count;

            Counter(Sequence seq, int count) {
                this.seq = seq;
                this.count = count;
            }

            @Override
            public void run() {
                try {
                    barrier.await(); // 使线程同时开始计数

                    for (int i = 0; i < count; i++) {
                        seq.getNext();
                    }

                    barrier.await(); // 使线程同时结束
                } catch ( Exception e ) {
                    throw new RuntimeException( e );
                }

            }

        }

    }
}
