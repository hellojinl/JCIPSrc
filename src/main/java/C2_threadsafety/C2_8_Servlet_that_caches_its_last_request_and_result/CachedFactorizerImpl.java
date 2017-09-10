package C2_threadsafety.C2_8_Servlet_that_caches_its_last_request_and_result;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;
import support.servlet.ServletResponseImpl;

/**
 * CachedFactorizer
 * <p/>
 * Servlet that caches its last request and result
 * --------------------------------------------------- 为了测试在它的基础上，补充了简单的实现，用于测试
 *
 */
@SuppressWarnings("serial")
@ThreadSafe
public class CachedFactorizerImpl extends GenericServlet implements Servlet {

    public static final String REQUEST_KEY = "number";
    public static final String RESPONSE_KEY = "factors";

    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;
    @GuardedBy("this")
    private long hits;
    @GuardedBy("this")
    private long cacheHits;

    public synchronized long getHits() {
        return hits;
    }

    public synchronized double getCacheHitRatio() {
        return (double) cacheHits / (double) hits;
    }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        BigInteger[] factors = null;
        synchronized ( this ) {
            ++hits;
            if (i.equals( lastNumber )) {
                ++cacheHits;
                factors = lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor( i );
            synchronized ( this ) {
                lastNumber = i;
                lastFactors = factors.clone();
            }
        }
        encodeIntoResponse( resp, factors );
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
