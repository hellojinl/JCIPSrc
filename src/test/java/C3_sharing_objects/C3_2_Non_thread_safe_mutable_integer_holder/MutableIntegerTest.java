package C3_sharing_objects.C3_2_Non_thread_safe_mutable_integer_holder;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

/**
 * 
 * 由于MutableInteger中的get和set方法都没有synchronized，且value也不是volatile，因此它是线程不安全的，理论上有可能出现
 * 在一个线程中将value值set为123456之后，在另一线程中读到的value值不等于12356（中间没有其他线程修改value值）。
 * 所以我想写一个测试让这个过程可以很容易的反复出现，可是没有成功。但是即使很难复现出错的情况，也不能说MutableInteger是线程安全的，
 * 它仍然是线程不安全的，此外实际生产环境中高并发是很常见的（测试环境很难模拟），因此MutableInteger在实际生产环境中出错的概率并不低，
 * 因此如果你不注意，就可能会遇到测试一切OK，上线就出问题的窘境。
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see <a href=
 *      "http://www.cnblogs.com/rocomp/p/4780532.html">细说Java多线程之内存可见性</a>
 */
public class MutableIntegerTest {

    @Test
    public void test() throws InterruptedException, BrokenBarrierException {

        final MutableInteger mutableInteger = new MutableInteger();
        final CountDownLatch latch = new CountDownLatch( 1 );

        ReaderThread readerThread = new ReaderThread( mutableInteger, latch );
        readerThread.start();

        WriterThread writerThread = new WriterThread( mutableInteger, latch );
        writerThread.start();

        readerThread.join();
    }

    private class WriterThread extends Thread {

        private final MutableInteger mutableInteger;
        private final CountDownLatch latch;

        WriterThread(MutableInteger mutableInteger, CountDownLatch latch) {
            this.mutableInteger = mutableInteger;
            this.latch = latch;
        }

        public void run() {
            mutableInteger.set( 123456 );
            latch.countDown();
        }
    }

    private class ReaderThread extends Thread {

        private final MutableInteger mutableInteger;
        private final CountDownLatch latch;

        ReaderThread(MutableInteger mutableInteger, CountDownLatch latch) {
            this.mutableInteger = mutableInteger;
            this.latch = latch;
        }

        public void run() {
            try {
                latch.await();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
                return; // 结束线程
            }
            System.out.println( mutableInteger.get() ); // 只有在极少的情况下（短时间，高并发），这个值才不为123456
        }
    }

}
