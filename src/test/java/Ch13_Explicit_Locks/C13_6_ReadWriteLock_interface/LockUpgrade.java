package Ch13_Explicit_Locks.C13_6_ReadWriteLock_interface;

import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;

/**
 * 锁升级：从读锁升级到写锁，将死锁
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LockUpgrade {

    private final static ExecutorService pool = Executors.newCachedThreadPool();

    @Test(expected = TimeoutException.class)
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        final MySimpleCounter2 counter = new MySimpleCounter2();

        Future< ? > future1 = pool.submit( () -> counter.print() );
        future1.get( 2, TimeUnit.SECONDS ); // 能够正常结束

        Future< ? > future2 = pool.submit( () -> counter.printAndIncrease() );
        future2.get( 2, TimeUnit.SECONDS ); // 运行足够的时间，都无法结束，抛出TimeoutException
        fail( "不应该执行到这，因为死锁" );
    }
}

class MySimpleCounter2 {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    private int count;

    public void printAndIncrease() {
        r.lock();
        try {
            System.out.println( String.format( "(printAndIncrease) Thread [%2d] count=%d",
                    Thread.currentThread().getId(), this.count ) );
        } finally {
            System.out.println( "(printAndIncrease) start waiting..." );
            w.lock(); // 读写锁是互斥的，因此将无法获得写锁，该线程将永远等待并且永远也不会释放读锁，该线程发送死锁
            r.unlock();
        }

        try {
            System.out.println( "(printAndIncrease) readLock unlock successfully" );
            count++;
        } finally {
            w.unlock();
        }
    }

    public void print() {
        r.lock();
        try {
            System.out.println(
                    String.format( "(print) Thread [%2d] count=%d", Thread.currentThread().getId(), this.count ) );
        } finally {
            r.unlock();
        }
    }
}
