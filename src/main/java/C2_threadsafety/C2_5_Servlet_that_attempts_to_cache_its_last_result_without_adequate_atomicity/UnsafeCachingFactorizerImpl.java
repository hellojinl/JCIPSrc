package C2_threadsafety.C2_5_Servlet_that_attempts_to_cache_its_last_result_without_adequate_atomicity;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.NotThreadSafe;
import support.servlet.ServletResponseImpl;
import support.sleep.Sleep;

/**
 * UnsafeCachingFactorizer
 *
 * Servlet that attempts to cache its last result without adequate atomicity
 * 
 * ----------------------------------------------------------------------------
 * 
 * UnsafeCachingFactorizer没有具体的实现，只能用于理解并发问题，这里我添加了其具体实现，使其可以进行单元测试
 * 注意：重点在线程安全性上，并没有严格的实现所有细节（这里并不重要），并且为了让测试效果更明显，在某些关键的地方加了休眠
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see C2_threadsafety.C2_5_Servlet_that_attempts_to_cache_its_last_result_without_adequate_atomicity.UnsafeCachingFactorizer
 */
@SuppressWarnings("serial")
@NotThreadSafe
public class UnsafeCachingFactorizerImpl extends GenericServlet implements Servlet {

    public static final String REQUEST_KEY = "number";
    public static final String RESPONSE_KEY = "factors";

    private final AtomicReference< BigInteger > lastNumber = new AtomicReference< BigInteger >();
    private final AtomicReference< BigInteger[] > lastFactors = new AtomicReference< BigInteger[] >();

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        if (i.equals( lastNumber.get() ))
            encodeIntoResponse( resp, lastFactors.get() );
        else {
            BigInteger[] factors = factor( i );
            lastNumber.set( i );

            // 并非逻辑代码，这里增加休眠是为了让测试效果更明显
            // 即，这里卡一下，很大可能将产生并发问题
            Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );

            lastFactors.set( factors );
            encodeIntoResponse( resp, factors );
        }
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
        if (factors == null) {
            return;
        }
        if (resp instanceof ServletResponseImpl) {
            ServletResponseImpl respImpl = (ServletResponseImpl) resp;
            respImpl.setAttribute( RESPONSE_KEY, factors );
        }
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return (BigInteger) req.getAttribute( REQUEST_KEY );
    }

    BigInteger[] factor(BigInteger aBigInteger) {
        // ！！因式分解，仅用于测试

        int num = aBigInteger.intValue();
        if (num == 1) {
            return new BigInteger[] { BigInteger.ONE };
        }

        List< BigInteger > list = new LinkedList< BigInteger >();
        for (int i = 2; i * i <= num; i++) {
            while ( num % i == 0 ) {
                list.add( BigInteger.valueOf( i ) );
                num = num / i;
            }
        }
        if (num > 1) {
            list.add( BigInteger.valueOf( num ) );
        }

        BigInteger[] result = new BigInteger[ list.size() ];
        return list.toArray( result );
    }
}
