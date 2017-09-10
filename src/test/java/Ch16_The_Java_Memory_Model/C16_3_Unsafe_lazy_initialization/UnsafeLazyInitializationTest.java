package Ch16_The_Java_Memory_Model.C16_3_Unsafe_lazy_initialization;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import Ch16_The_Java_Memory_Model.C16_3_Unsafe_lazy_initialization.UnsafeLazyInitializationTest.UnsafeLazyInitialization.Resource;
import support.annotations.Evaluated;
import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class UnsafeLazyInitializationTest {

    ExecutorService pool = Executors.newCachedThreadPool();

    @Test
    public void test() {
        assertTrue( ConcurrentTestExecutor.repeatedExecute( () -> {
            Future< Resource > f1 = pool.submit( new CreateCallable() );
            Future< Resource > f2 = pool.submit( new CreateCallable() );

            Resource r1 = f1.get();
            Resource r2 = f2.get();

            return r1 != r2; // 期望的结果为两个实例不相等
        } ) );
    }

    class CreateCallable implements Callable< Resource > {

        @Override
        public Resource call() throws Exception {
            return UnsafeLazyInitialization.getInstance();
        }

    }

    static class UnsafeLazyInitialization {
        private static Resource resource;

        @Evaluated(">_<")
        public static Resource getInstance() {
            if (resource == null) {
                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS ); // 为了测试效果明显
                resource = new Resource(); // unsafe publication
            }
            return resource;
        }

        static class Resource {
        }
    }
}
