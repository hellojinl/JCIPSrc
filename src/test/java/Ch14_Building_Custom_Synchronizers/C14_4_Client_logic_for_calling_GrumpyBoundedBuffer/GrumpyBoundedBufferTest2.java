package Ch14_Building_Custom_Synchronizers.C14_4_Client_logic_for_calling_GrumpyBoundedBuffer;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class GrumpyBoundedBufferTest2 {

    private final static ExecutorService pool = Executors.newCachedThreadPool();

    private GrumpyBoundedBuffer< String > buffer = new GrumpyBoundedBuffer< String >();
    int SLEEP_GRANULARITY = 50;

    @Test
    public void test_useBuff() throws InterruptedException, ExecutionException {
        Future< String > takeResult = pool.submit( new Callable< String >() {

            @Override
            public String call() throws Exception {
                // 调用GrumpyBoundedBuffer的客户端代码演示
                // 1.捕获异常
                // 2.重试
                while ( true ) {
                    try {
                        String item = buffer.take();
                        // use item
                        return item;
                    } catch ( BufferEmptyException e ) {
                        Thread.sleep( SLEEP_GRANULARITY );
                    }
                }
            }

        } );

        pool.execute( () -> buffer.put( "a msg" ) );

        assertEquals( "a msg", takeResult.get() );

    }
}
