package C4_composing_objects.Ch4_11_Thread_safe_mutable_point_class;

import org.junit.Test;

import support.annotations.GuardedBy;

/**
 * 阐述了编译错误的写法 并说明了如何将编译错误的写法改造成正确的写法
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SafePointCompilerErrorTest {

    @Test
    public void test() {
    }

    public class SafePoint {
        @GuardedBy("this")
        private int x, y;

        // 编译错误：Constructor call must be the first statement in a constructor
        // public SafePoint(SafePoint p) {
        // int[] xy = p.get();
        // this( xy[0], xy[1] ); // this/super在构造函数中始终未第一句
        // }

        public SafePoint(SafePoint p) {
            int[] xy = p.get();
            this.set( xy[0], xy[1] );

            // 这么写也是线程安全的，
            // 但是假如你就是想要把上面的编译错误改对了，就是要使用this( xy[0], xy[1] )该怎么做呢？
            // 当然也是可以做到的，只不过要把原来的构造函数一分为二，引入另一个构造函数，如下所示
            // private SafePoint(int[] a) {
            // this(a[0], a[1]); // 现在符合语法了，它是第一行，同时也满足了你的需求，使用了this( xy[0],
            // xy[1] )
            // }
            // public SafePoint(SafePoint p) {
            // this(p.get());
            // }
            // 你可能会有两个问题：
            // 1.为什么一定要引入构造函数？
            // 答：之所以一定要引入构造函数，是因为你固执的坚持要使用this( xy[0], xy[1]
            // )，假如换一种思路（比如现在的实现）这个就不是必须的
            // 2.引入的构造函数为什么必须是私有的
            // 答：限定它只能内部使用，如果外部能使用它，那么int[] a将导致线程不安全，因为它是一个引用变量，外部能够获得它，从而修改它的值
            // 改造完成后的代码就是C4_composing_objects.Ch4_11_Thread_safe_mutable_point_class.SafePoint
            // 注：书本给出的源代码并不比这里的优秀，它的侧重点在于说明原理，但是非常难理解
        }

        public SafePoint(int x, int y) {
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
