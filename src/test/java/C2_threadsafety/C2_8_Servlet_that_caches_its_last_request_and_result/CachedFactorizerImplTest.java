package C2_threadsafety.C2_8_Servlet_that_caches_its_last_request_and_result;

import static org.junit.Assert.assertEquals;
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

import support.servlet.ServletRequestImpl;
import support.servlet.ServletResponseImpl;

/**
 * 验证CachedFactorizer的线程安全性
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CachedFactorizerImplTest {

    private static final long timeout = 1;
    private static final TimeUnit timeUnit = TimeUnit.SECONDS;

    private static final BigInteger SIXTY = BigInteger.valueOf( 60 );
    private static final BigInteger ONE_HUNDRED = BigInteger.valueOf( 100 );

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        final CachedFactorizerImpl servlet = new CachedFactorizerImpl();
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

        // 校验hits
        assertEquals( numArray.length, servlet.getHits() );

        // cacheHitRatio具有随机性和numArray中值得排列顺序无关
        System.out.println( "cacheHitRatio=" + servlet.getCacheHitRatio() );
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
