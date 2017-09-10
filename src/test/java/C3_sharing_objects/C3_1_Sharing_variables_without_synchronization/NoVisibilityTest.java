package C3_sharing_objects.C3_1_Sharing_variables_without_synchronization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * 测试没用同步的情况下，不同线程间对共享变量的可见性问题
 * 
 * 书上说：number可能会输出0，读线程可能永远无法看到其他线程写入的ready值， TODO
 * 我想设计一个用例，让这种情况可以反复的出现，而不仅仅是理论，但如下的设计显然没做到，目前也想不出任何更好的办法 >_<
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class NoVisibilityTest {

    private static boolean ready;
    private static int number;

    private final static int threadsCount = 100;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final static CyclicBarrier barrier = new CyclicBarrier( threadsCount + 1 );

    private static class ReaderCallable implements Callable< Integer > {

        private int numberInReaderThread;

        @Override
        public Integer call() throws Exception {
            barrier.await();

            for (int i = 0; !ready && i < 100000; i++) {
                Thread.yield();
            }
            if (!ready) {
                throw new RuntimeException( "ready的改变不可见" );
            }
            numberInReaderThread = number;
            return Integer.valueOf( numberInReaderThread );
        }

    }

    @Test
    public void test() throws InterruptedException, BrokenBarrierException, ExecutionException {
        final List< Future< Integer > > futureList = new ArrayList< Future< Integer > >();
        for (int i = 0; i < threadsCount; i++) {
            Future< Integer > future = executor.submit( new ReaderCallable() );
            futureList.add( future );
        }
        barrier.await();
        number = 42;
        ready = true;

        for (Future< Integer > future : futureList) {
            Integer result = future.get();
            if (result != 42) {
                System.out.println( result );
            }
        }
    }
}
