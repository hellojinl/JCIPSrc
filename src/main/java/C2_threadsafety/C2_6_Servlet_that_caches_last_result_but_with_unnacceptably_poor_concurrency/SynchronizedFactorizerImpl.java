package C2_threadsafety.C2_6_Servlet_that_caches_last_result_but_with_unnacceptably_poor_concurrency;

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
 * SynchronizedFactorizer
 *
 * Servlet that caches last result, but with unnacceptably poor concurrency
 *
 * ----------------------------------------------------------------------------
 * 
 * SynchronizedFactorizer没有具体的实现，只能用于理解并发问题，这里我添加了具体实现，使其可以进行单元测试
 * 注意：重点在线程安全性上，并没有严格的实现所有细节（这里并不重要）
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@SuppressWarnings("serial")
@ThreadSafe
public class SynchronizedFactorizerImpl extends GenericServlet implements Servlet {

    public static final String REQUEST_KEY = "number";
    public static final String RESPONSE_KEY = "factors";

    @GuardedBy("this")
    private BigInteger lastNumber;
    @GuardedBy("this")
    private BigInteger[] lastFactors;

    public synchronized void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        if (i.equals( lastNumber ))
            encodeIntoResponse( resp, lastFactors );
        else {
            BigInteger[] factors = factor( i );
            lastNumber = i;
            lastFactors = factors;
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
