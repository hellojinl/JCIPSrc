package C2_threadsafety.C2_3_Race_condition_in_lazy_initialization;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * 多线程环境下验证SafeLazyInitRace是线程不安全的
 * <p>
 * 如果它是线程安全的，多个线程将返回同一个实例
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SafeLazyInitRaceTest {
    private static final long timeout = 10000;
    private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        final SafeLazyInit lazyInitRace = new SafeLazyInit();
        List< Future< ExpensiveObject > > futureList = new ArrayList< Future< ExpensiveObject > >();
        for (int i = 0; i < threadCount; i++) {
            Future< ExpensiveObject > future = executor.submit( new Callable< ExpensiveObject >() {

                @Override
                public ExpensiveObject call() throws Exception {
                    return lazyInitRace.getInstance();
                }

            } );
            futureList.add( future );
        }

        boolean hasDifferences = false;
        ExpensiveObject base = null;
        for (Future< ExpensiveObject > future : futureList) {
            if (base == null) {
                base = future.get( timeout, timeUnit );
            } else {
                ExpensiveObject newObject = future.get( timeout, timeUnit );
                if (base != newObject) {
                    hasDifferences = true;
                }
            }
        }

        assertFalse( hasDifferences );
    }
}
