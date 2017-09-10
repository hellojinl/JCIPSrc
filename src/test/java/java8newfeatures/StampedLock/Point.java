package java8newfeatures.StampedLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

import javax.annotation.Generated;

import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class Point {

    private final StampedLock lock = new StampedLock();
    @Generated("lock")
    private int x, y;

    Point() {

    }

    Point(int x, int y) {
        set( x, y );
    }

    public void set(int x, int y) {
        System.out.println( "step in set(...)" );

        final long stamp = lock.writeLock();
        System.out.println( "writeLock stamp = " + stamp );
        try {
            this.x = x;
            this.y = y;
        } finally {
            lock.unlockWrite( stamp );
        }

        System.out.println( "step out set(...)" );
    }

    @Override
    public String toString() {
        System.out.println( "step in toString()" );

        try {
            final long optiStamp = lock.tryOptimisticRead(); // 由于tryOptimisticRead并没有使用CAS设置锁状态所以不需要显示的释放该锁

            System.out.println( "tryOptimisticRead stamp = " + optiStamp );

            String result = String.format( "[%d, %d]", this.x, this.y );

            Sleep.sleepUninterruptibly( 4, TimeUnit.SECONDS ); // 一个延迟，为了演示效果

            if (lock.validate( optiStamp )) {
                return result;
            } else {
                long stamp = lock.readLock();
                System.out.println( "readLock stamp = " + stamp );
                try {
                    return String.format( "[%d, %d]", this.x, this.y );
                } finally {
                    lock.unlockRead( stamp );
                }
            }
        } finally {
            System.out.println( "step out toString()" );
        }

    }
}
