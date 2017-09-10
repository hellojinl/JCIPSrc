package C5_Building_Blocks.Ch5_20_Factorizing_servlet_that_caches_results_using_Memoizer;

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

import javax.servlet.ServletRequest;

import org.junit.Test;

import C2_threadsafety.C2_8_Servlet_that_caches_its_last_request_and_result.CachedFactorizerImpl;
import support.PrintUtil;
import support.servlet.ServletRequestImpl;
import support.servlet.ServletResponseImpl;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class FactorizerTest {

    private static final long timeout = 10;
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;

    private static final BigInteger SIXTY = BigInteger.valueOf( 60 );
    private static final BigInteger ONE_HUNDRED = BigInteger.valueOf( 100 );

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        final FactorizerImpl servlet = new FactorizerImpl();
        final List< Future< Boolean > > futureList = new ArrayList< Future< Boolean > >();

        final BigInteger[] numArray = new BigInteger[] { ONE_HUNDRED, SIXTY, ONE_HUNDRED, SIXTY,
                BigInteger.valueOf( 53 ), ONE_HUNDRED, ONE_HUNDRED, ONE_HUNDRED, SIXTY, SIXTY, SIXTY };

        for (int i = 0; i < numArray.length; i++) {
            final BigInteger num = numArray[i];
            Future< Boolean > future = executor.submit( new Callable< Boolean >() {

                @Override
                public Boolean call() throws Exception {
                    ServletRequest req = new ServletRequestImpl();
                    req.setAttribute( CachedFactorizerImpl.REQUEST_KEY, num );
                    ServletResponseImpl res = new ServletResponseImpl();

                    servlet.service( req, res );

                    BigInteger[] result = (BigInteger[]) res.getAttribute( CachedFactorizerImpl.RESPONSE_KEY );

                    System.out.println( "factor( " + num + " ) = " + PrintUtil.arrayToString( result ) );
                    return eq( num, result );
                }

            } );

            futureList.add( future );
        }

        // 校验计算结果是否全部正确
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
