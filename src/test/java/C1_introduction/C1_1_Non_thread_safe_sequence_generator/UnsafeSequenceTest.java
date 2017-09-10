package C1_introduction.C1_1_Non_thread_safe_sequence_generator;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import testUtils.ConcurrentTestExecutor;

/**
 * 多线程环境下，验证UnsafeSequence是线程不安全的（即，期望值不等于实际结果）
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class UnsafeSequenceTest {

    @Test
    public void test() {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {

            UnsafeSequence seq = new UnsafeSequence();
            int threadsCount = Runtime.getRuntime().availableProcessors() + 1; // 比你的cpu个数多1，让线程进行上下文切换
            int executionTimesPerThread = 1000;
            int expected = threadsCount * executionTimesPerThread; // 你想要的结果

            TestUnsafeSequenceInMultithread t = new TestUnsafeSequenceInMultithread( seq, threadsCount,
                    executionTimesPerThread );
            int actual = t.execute(); // 实际结果

            if (expected != actual) {
                System.out.println( "[" + this.getClass().getSimpleName() + "] the count of threads is " + threadsCount
                        + ", the execution times per thread are " + executionTimesPerThread + ", the expected value is "
                        + expected + ", the actual value is " + actual );
                return true; // 线程不安全，两个值应该不相等是我们期望中的结果
            } else {
                return false; // 虽然expected ==
                              // actual也是对的，但并不是我们测试期望的结果，所以返回false
            }

        } ) );

    }

    private static class TestUnsafeSequenceInMultithread {

        private static final ExecutorService pool = Executors.newCachedThreadPool();

        private final CyclicBarrier barrier;
        private final UnsafeSequence seq;
        private final int threadsCount;
        private final int executionTimesPerThread;

        TestUnsafeSequenceInMultithread(UnsafeSequence seq, int threadsCount, int executionTimesPerThread) {
            this.seq = seq;
            this.threadsCount = threadsCount;
            this.barrier = new CyclicBarrier( threadsCount + 1 ); // 这里的 + 1
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

            private final UnsafeSequence seq;
            private final int count;

            Counter(UnsafeSequence seq, int count) {
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
