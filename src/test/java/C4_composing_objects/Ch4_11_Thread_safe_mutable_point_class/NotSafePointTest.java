package C4_composing_objects.Ch4_11_Thread_safe_mutable_point_class;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.annotations.GuardedBy;
import support.annotations.NotThreadSafe;
import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;

public class NotSafePointTest {

    @Test
    public void test() throws InterruptedException {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {

            final NotSafePoint p1 = new NotSafePoint( 5, 5 );
            Thread t1 = new Thread() {
                @Override
                public void run() {
                    p1.set( 10, 10 );
                }
            };
            t1.start();

            TimeUnit.SECONDS.sleep( 1 );

            NotSafePoint p2 = new NotSafePoint( p1 );
            t1.join();

            int[] xy = p2.get();
            System.out.println( xy[0] + ", " + xy[1] );

            return xy[0] != xy[1]; // 这里x != y表示状态被破坏了

        } ) );

    }
}

@NotThreadSafe
class NotSafePoint {
    @GuardedBy("this")
    private int x, y;

    public NotSafePoint(NotSafePoint p) {
        this( p.x, p.y );

        // 这里是线程不安全的，因为在构造函数里:
        // p.x 和 p.y 这两个读取操作没有任何同步
        // 另一个线程可能会调用p.set方法
        // 读取没同步，即使修改有同步，也会出错
    }

    public NotSafePoint(int x, int y) {
        this.set( x, y );
    }

    public synchronized int[] get() {
        return new int[] { x, y };
    }

    public synchronized void set(int x, int y) {
        this.x = x;

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 为了测试效果

        this.y = y;
    }
}