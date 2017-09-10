package C5_Building_Blocks.Ch5_20_Factorizing_servlet_that_caches_results_using_Memoizer;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import C5_Building_Blocks.Ch5_19_Final_implementation_of_Memoizer.Memoizer;
import support.Computable;
import support.servlet.ServletResponseImpl;

/**
 *
 * Factorizer
 * <p/>
 * Factorizing servlet that caches results using Memoizer
 * --------------------------------------------------- 为了测试在它的基础上，补充了简单的实现，用于测试
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@SuppressWarnings("serial")
public class FactorizerImpl extends GenericServlet implements Servlet {

    public static final String REQUEST_KEY = "number";
    public static final String RESPONSE_KEY = "factors";
    public static final String ERROR_KEY = "error";

    private final Computable< BigInteger, BigInteger[] > c = new Computable< BigInteger, BigInteger[] >() {
        public BigInteger[] compute(BigInteger arg) {
            return factor( arg );
        }
    };
    private final Computable< BigInteger, BigInteger[] > cache = new Memoizer< BigInteger, BigInteger[] >( c );

    public void service(ServletRequest req, ServletResponse resp) {
        try {
            BigInteger i = extractFromRequest( req );
            encodeIntoResponse( resp, cache.compute( i ) );
        } catch ( InterruptedException e ) {
            encodeError( resp, "factorization interrupted" );
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

    void encodeError(ServletResponse resp, String errorString) {
        if (resp instanceof ServletResponseImpl) {
            ServletResponseImpl respImpl = (ServletResponseImpl) resp;
            respImpl.setAttribute( ERROR_KEY, errorString );
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
