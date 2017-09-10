package Ch14_Building_Custom_Synchronizers.C14_8_Using_conditional_notification_in_BoundedBuffer_put;

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
public class BoundedBufferTest {

    private final static ExecutorService pool = Executors.newCachedThreadPool();
    BoundedBuffer< String > buffer = new BoundedBuffer<>();

    @Test
    public void test() throws InterruptedException, ExecutionException {
        Future< String > takeResult = pool.submit( new Callable< String >() {

            @Override
            public String call() throws Exception {
                return buffer.take();
            }

        } );

        pool.execute( () -> {
            try {
                buffer.alternatePut( "a msg" );
            } catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        } );

        assertEquals( "a msg", takeResult.get() );
    }

}
