package C8_Applying_Thread_Pools.C8_8_Modifying_an_Executor_created_with_the_standard_factories;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;

/**
 * 测试无法修改的ExecutorService，
 * 思路就是在原始ThreadPoolExecutor外封装一层，使用委托的方式使其只暴露ExecutorService接口中的方法，
 * 只要包装类不提供任何方法返回原始ThreadPoolExecutor的对象，那么外部将无法修改执行策略
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class UnableModifyTest {

    @Test(expected = AssertionError.class)
    public void test() {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExec = (ThreadPoolExecutor) exec;
            threadPoolExec.setCorePoolSize( 10 );
            assertEquals( 10, threadPoolExec.getCorePoolSize() );
        } else {
            throw new AssertionError( "Oops, bad assumption" );
        }
    }
}
