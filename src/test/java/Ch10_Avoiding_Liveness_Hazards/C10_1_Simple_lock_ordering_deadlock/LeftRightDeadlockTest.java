package Ch10_Avoiding_Liveness_Hazards.C10_1_Simple_lock_ordering_deadlock;

import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LeftRightDeadlockTest {

    @Test
    public void test() {
        final LeftRightDeadlock deadlock = new LeftRightDeadlock();
        Thread leftRight = new Thread( () -> {
            deadlock.leftRight();
        } );
        Thread rightLeft = new Thread( () -> {
            deadlock.rightLeft();
        } );
        leftRight.start();
        rightLeft.start();

        // 执行足够的时间
        Sleep.sleepUninterruptibly( 4, TimeUnit.SECONDS );

        // 检测死锁
        assertFalse( deadlock.leftRight );
        assertFalse( deadlock.rightLeft );

    }

    class LeftRightDeadlock {
        private final Object left = new Object();
        private final Object right = new Object();

        private boolean leftRight = false;
        private boolean rightLeft = false;

        public void leftRight() {
            synchronized ( left ) {
                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
                synchronized ( right ) {
                    doSomething();
                }
            }
        }

        public void rightLeft() {
            synchronized ( right ) {
                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
                synchronized ( left ) {
                    doSomethingElse();
                }
            }
        }

        void doSomething() {
            leftRight = true;
        }

        void doSomethingElse() {
            rightLeft = true;
        }
    }

}
