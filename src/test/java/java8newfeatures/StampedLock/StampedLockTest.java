package java8newfeatures.StampedLock;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.sleep.Sleep;

/**
 * 1. [thread 1]读数据线程，申请乐观读锁 2. [thread 2]写数据线程，申请写锁，修改数据，释放写锁 3. [thread
 * 1]读数据线程，验证stamp发现有修改 4. [thread 1]读数据线程，申请悲观读锁（和写操作互斥） 5. [thread
 * 1]读数据线程，返回结果，释放悲观读锁
 * 
 * 即读数据的时候，允许写数据，但之后要检查并重读数据，见{@link Point}
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class StampedLockTest {

    private final ExecutorService pool = Executors.newCachedThreadPool();
    Point point = new Point();

    @Test
    public void test() throws InterruptedException, ExecutionException {

        Future< String > future = pool.submit( this::read );
        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
        pool.execute( this::write );

        String str = future.get();
        System.out.println( str );
        assertEquals( "[100, 100]", str );
    }

    private String read() {
        return point.toString();
    }

    private void write() {
        point.set( 100, 100 );
    }
}
