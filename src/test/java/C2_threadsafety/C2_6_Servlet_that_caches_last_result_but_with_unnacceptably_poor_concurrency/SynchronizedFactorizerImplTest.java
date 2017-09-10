package C2_threadsafety.C2_6_Servlet_that_caches_last_result_but_with_unnacceptably_poor_concurrency;

import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;

import org.junit.Test;

import support.servlet.ServletRequestImpl;
import support.servlet.ServletResponseImpl;

/**
 * SynchronizedFactorizer是线程安全的，验证其线程安全性，此外它的并发效率不好
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SynchronizedFactorizerImplTest {

    private static final long timeout = 5;
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    @Test
    public void test_in_multithread() throws InterruptedException, ExecutionException, TimeoutException {
        final Servlet servlet = new SynchronizedFactorizerImpl();
        final List< Future< Boolean > > futureList = new ArrayList< Future< Boolean > >();

        for (int i = 0; i < threadCount; i++) {
            Future< Boolean > future = executor.submit( new Callable< Boolean >() {

                @Override
                public Boolean call() throws Exception {
                    ServletRequest req = new ServletRequestImpl();
                    req.setAttribute( SynchronizedFactorizerImpl.REQUEST_KEY, BigInteger.valueOf( 100 ) );
                    ServletResponseImpl res = new ServletResponseImpl();

                    servlet.service( req, res );

                    BigInteger[] result = (BigInteger[]) res.getAttribute( SynchronizedFactorizerImpl.RESPONSE_KEY );
                    return eq( BigInteger.valueOf( 100 ), result );
                }

            } );

            futureList.add( future );
        }

        boolean hasError = false;
        for (Future< Boolean > future : futureList) {
            Boolean result = future.get( timeout, timeUnit );
            if (!result) {
                hasError = true;
            }
        }

        assertFalse( hasError );

    }

    private boolean eq(BigInteger num, BigInteger[] factors) {
        if (num == null || factors == null) {
            return false;
        }
        int expected = num.intValue();
        int actual = 1;
        for (int i = 0; i < factors.length; i++) {
            actual *= factors[i].intValue();
        }
        return expected == actual;
    }
}
