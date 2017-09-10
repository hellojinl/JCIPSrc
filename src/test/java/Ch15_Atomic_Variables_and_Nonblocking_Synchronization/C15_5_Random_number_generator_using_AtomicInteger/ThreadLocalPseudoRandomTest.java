package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_5_Random_number_generator_using_AtomicInteger;

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
public class ThreadLocalPseudoRandomTest {

    ThreadLocalPseudoRandom random = new ThreadLocalPseudoRandom( 1000 );

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            new Thread( new PrintRunnable() ).start();
        }

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
    }

    class PrintRunnable implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println( String.format( "Thread[%2d] i=%d, random = %d", Thread.currentThread().getId(), i,
                        random.nextInt( 1000 ) ) );
            }
        }

    }
}

/*
 * 某次运行结果，手动排序之后的结果如下， 每个线程都只能看见自己私有的伪随机数字序列，而不是所有线程共享同一个随机数序列
 * 
 * Thread[ 9] i=0, random = 1000 Thread[ 9] i=1, random = 24 Thread[ 9] i=2,
 * random = 491 Thread[ 9] i=3, random = 860 Thread[ 9] i=4, random = 814
 * 
 * Thread[10] i=0, random = 1000 Thread[10] i=1, random = 24 Thread[10] i=2,
 * random = 491 Thread[10] i=3, random = 860 Thread[10] i=4, random = 814
 * 
 * Thread[11] i=0, random = 1000 Thread[11] i=1, random = 24 Thread[11] i=2,
 * random = 491 Thread[11] i=3, random = 860 Thread[11] i=4, random = 814
 * 
 * Thread[12] i=0, random = 1000 Thread[12] i=1, random = 24 Thread[12] i=2,
 * random = 491 Thread[12] i=3, random = 860 Thread[12] i=4, random = 814
 * 
 * Thread[13] i=0, random = 1000 Thread[13] i=1, random = 24 Thread[13] i=2,
 * random = 491 Thread[13] i=3, random = 860 Thread[13] i=4, random = 814
 */
