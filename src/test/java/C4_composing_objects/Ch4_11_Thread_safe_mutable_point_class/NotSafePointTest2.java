package C4_composing_objects.Ch4_11_Thread_safe_mutable_point_class;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.annotations.GuardedBy;
import support.annotations.NotThreadSafe;
import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;

public class NotSafePointTest2 {

    @Test
    public void test() throws InterruptedException, BrokenBarrierException, ExecutionException {
        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {
            final CyclicBarrier barrier = new CyclicBarrier( 3 );
            final int[] a = { 5, 5 };
            Thread t1 = new Thread() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }

                    // 这里对a[0],a[1]的更新没有任何同步保障（而在Point里将x,y视为一个整体）
                    a[1] = 10;
                    Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
                    a[0] = 10;
                }
            };
            t1.start();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future< Boolean > future = executor.submit( new Callable< Boolean >() {

                @Override
                public Boolean call() throws Exception {
                    try {
                        barrier.await();
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }

                    // 有如下两种线程不安全的情况：
                    // 1. 当a被修改为[5, 10]之后赋值给Point
                    // （从纯理论上说这种情况并不影响对Point线程安全性的判断，理论上可以认为你就是要使用a=[5,
                    // 10]来构造Point，且结果也是x=5,y=10，这没有错，
                    // 但是从实际应用角度看，这显然不是你想要的结果，你所期待的结果应该是[5,5]或[10,10]，至于[5,10]这不是你想要的，你会感到很惊讶）
                    //
                    // 2.当a=[5,
                    // 5]时赋值给Point，然后在构造的过程中，读取完x[0]之后，t1线程将x[1]修改为10，这种情况无论从理论还是实际应用都是错的,因为将会出现如下代码序列
                    // p = new Point([5, 5])
                    // p.x = 5, p.y = 10 <= 不合逻辑

                    Point p = new Point( a );
                    int[] xy = p.get();
                    System.out.println( xy[0] + ", " + xy[1] );
                    return xy[0] != xy[1];
                }

            } );

            barrier.await();
            return future.get();

        } ) );

    }

    @NotThreadSafe
    public class Point {
        @GuardedBy("this")
        private int x, y;

        public Point(int[] a) { // 这里的public将导致线程安全问题
            this( a[0], a[1] );
        }

        public Point(Point p) {
            this( p.get() );
        }

        public Point(int x, int y) {
            this.set( x, y );
        }

        public synchronized int[] get() {
            return new int[] { x, y };
        }

        public synchronized void set(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
