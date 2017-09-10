package C8_Applying_Thread_Pools.C8_8_Modifying_an_Executor_created_with_the_standard_factories;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ModifyExecutorTest {

    @Test
    public void test() {
        ExecutorService exec = Executors.newCachedThreadPool();
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExec = (ThreadPoolExecutor) exec;
            threadPoolExec.setCorePoolSize( 10 );
            assertEquals( 10, threadPoolExec.getCorePoolSize() );
        } else {
            throw new AssertionError( "Oops, bad assumption" );
        }
    }
}
