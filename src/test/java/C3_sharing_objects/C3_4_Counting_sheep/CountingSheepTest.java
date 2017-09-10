package C3_sharing_objects.C3_4_Counting_sheep;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * volatile变量可见性测试
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CountingSheepTest {

    @Test
    public void test() throws InterruptedException, ExecutionException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final CountingSheepImpl countingSheep = new CountingSheepImpl();

        // 另一个线程开始数羊
        Future< Boolean > future = executor.submit( new Callable< Boolean >() {

            @Override
            public Boolean call() throws Exception {
                return countingSheep.tryToSleep();
            }

        } );

        TimeUnit.SECONDS.sleep( 6 );

        // 另一个线程的countingSheep.tryToSleep()方法如果对asleep的改变不可见，那么这里将是一个无限循环。
        // asleep由于有volatile修饰，所以其他线程都能正确的看见asleep的改变，因此程序会正常的终止
        countingSheep.setAsleep( true );

        Boolean result = future.get();

        assertTrue( result );
    }

    /**
     * Counting sheep具体实现，为了更好的测试
     *
     * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
     *         Andals and the Rhoynar and the First Men, Lord of the Seven
     *         Kingdoms, Protector of the Realm, Caho of the Great Grass Sea,
     *         Breaker of Shackles, Father of Dragons.
     */
    class CountingSheepImpl {
        private volatile boolean asleep = false;
        private final AtomicInteger sheepCount = new AtomicInteger();

        public void setAsleep(boolean asleep) {
            this.asleep = asleep;
        }

        /**
         * 尝试睡觉
         * 
         * @return true - 睡着了, false - 永远不会返回false，要么返回true，要么永远执行下去
         */
        public boolean tryToSleep() throws InterruptedException {
            while ( !asleep )
                countSomeSheep();
            System.out.println( "fell asleep!" );
            return true;
        }

        private void countSomeSheep() throws InterruptedException {
            int num = sheepCount.incrementAndGet();
            System.out.println( num + " sheep" );
            TimeUnit.SECONDS.sleep( 1 );
        }
    }

}
