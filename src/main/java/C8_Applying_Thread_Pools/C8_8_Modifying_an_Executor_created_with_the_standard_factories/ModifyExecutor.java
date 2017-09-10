package C8_Applying_Thread_Pools.C8_8_Modifying_an_Executor_created_with_the_standard_factories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ModifyExecutor {

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        if (exec instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) exec).setCorePoolSize( 10 );
        } else {
            throw new AssertionError( "Oops, bad assumption" );
        }
    }
}
