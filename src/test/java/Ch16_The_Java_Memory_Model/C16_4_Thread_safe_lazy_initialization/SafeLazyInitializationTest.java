package Ch16_The_Java_Memory_Model.C16_4_Thread_safe_lazy_initialization;

import static org.junit.Assert.assertFalse;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import Ch16_The_Java_Memory_Model.C16_4_Thread_safe_lazy_initialization.SafeLazyInitializationTest.SafeLazyInitialization.Resource;
import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SafeLazyInitializationTest {

    ExecutorService pool = Executors.newCachedThreadPool();

    @Test
    public void test() {
        assertFalse( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                Future< Resource > f1 = pool.submit( new CreateCallable() );
                Future< Resource > f2 = pool.submit( new CreateCallable() );

                Resource r1 = f1.get();
                Resource r2 = f2.get();

                return r1 != r2; // 期望的结果为两个实例不相等
            }

            public int maximumExecutionTimes() {
                return 3; // 期望结果是永远无法得到的，减少实验次数
            }

        } ) );
    }

    class CreateCallable implements Callable< Resource > {

        @Override
        public Resource call() throws Exception {
            return SafeLazyInitialization.getInstance();
        }

    }

    static class SafeLazyInitialization {
        private static Resource resource;

        public synchronized static Resource getInstance() {
            if (resource == null) {
                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
                resource = new Resource();
            }
            return resource;
        }

        static class Resource {
        }
    }
}
