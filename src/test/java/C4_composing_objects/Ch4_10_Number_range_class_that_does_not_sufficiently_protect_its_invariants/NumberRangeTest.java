package C4_composing_objects.Ch4_10_Number_range_class_that_does_not_sufficiently_protect_its_invariants;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import testUtils.ConcurrentTestExecutor;

public class NumberRangeTest {

    @Test
    public void test() throws InterruptedException {
        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {
            // init
            final NumberRangeImpl range = new NumberRangeImpl( 50, 100 );

            // thread1 range.setLower(70)
            Thread thread1 = new Thread() {
                @Override
                public void run() {
                    try {
                        range.setLower( 70 );
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            };
            thread1.start();

            // thread2 range.setUpper(60)
            Thread thread2 = new Thread() {
                @Override
                public void run() {
                    range.setUpper( 60 );
                }
            };
            thread2.start();

            thread1.join();
            thread2.join();

            System.out.println( "lower = " + range.getLower() + ", upper = " + range.getUpper() );
            return range.getLower() > range.getUpper();

        } ) );

    }

    class NumberRangeImpl {
        // INVARIANT: lower <= upper
        private final AtomicInteger lower = new AtomicInteger( 0 );
        private final AtomicInteger upper = new AtomicInteger( 0 );

        NumberRangeImpl(int lower, int upper) {
            if (lower > upper) {
                throw new IllegalArgumentException( "can't set lower > upper" );
            }
            this.lower.set( lower );
            this.upper.set( upper );
        }

        public void setLower(int i) throws InterruptedException {
            // Warning -- unsafe check-then-act
            if (i > upper.get())
                throw new IllegalArgumentException( "can't set lower to " + i + " > upper" );

            TimeUnit.SECONDS.sleep( 3 ); // 为了测试效果，人为的设置一个定时阻塞

            lower.set( i );
        }

        public void setUpper(int i) {
            // Warning -- unsafe check-then-act
            if (i < lower.get())
                throw new IllegalArgumentException( "can't set upper to " + i + " < lower" );
            upper.set( i );
        }

        public boolean isInRange(int i) {
            return (i >= lower.get() && i <= upper.get());
        }

        // 以下get方法仅仅用于测试结果的判断（不要用于并发场景）

        public int getLower() {
            return lower.get();
        }

        public int getUpper() {
            return upper.get();
        }

        // 一种可能出错的执行序列
        // [init] range:NumberRange (lower = 50, upper = 100)
        // [thread1 range.setLower(70)] i > upper.get() => 70 > 100 => false
        // [thread2 range.setUpper(60)] i < lower.get() => 60 < 50 => false
        // [thread2 range.setUpper(60)] upper = 60
        // [thread1 range.setLower(70)] lower = 70
        // [error state] lower > upper
        // 主要问题在于（if条件判断完之后并非立即执行接下来的语句，当前线程有可能被挂起，
        // 然后切换到其他线程，当其他线程执行完返回当前线程之后，if条件所做的判断有可能已经失效了，然后再根据失效的判断执行操作就有可能出现错误）

    }
}
