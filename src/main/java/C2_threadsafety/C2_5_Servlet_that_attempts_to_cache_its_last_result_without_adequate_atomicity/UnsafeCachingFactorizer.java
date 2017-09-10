package C2_threadsafety.C2_5_Servlet_that_attempts_to_cache_its_last_result_without_adequate_atomicity;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.NotThreadSafe;

/**
 * UnsafeCachingFactorizer
 *
 * Servlet that attempts to cache its last result without adequate atomicity
 *
 * @author Brian Goetz and Tim Peierls
 */

@SuppressWarnings("serial")
@NotThreadSafe
public class UnsafeCachingFactorizer extends GenericServlet implements Servlet {
    private final AtomicReference< BigInteger > lastNumber = new AtomicReference< BigInteger >();
    private final AtomicReference< BigInteger[] > lastFactors = new AtomicReference< BigInteger[] >();

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        if (i.equals( lastNumber.get() ))
            encodeIntoResponse( resp, lastFactors.get() );
        else {
            BigInteger[] factors = factor( i );
            lastNumber.set( i );
            lastFactors.set( factors );
            encodeIntoResponse( resp, factors );
        }
    }

    void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger( "7" );
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[] { i };
    }
}
