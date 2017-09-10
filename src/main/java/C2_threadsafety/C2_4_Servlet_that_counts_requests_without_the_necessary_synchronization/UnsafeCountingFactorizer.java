package C2_threadsafety.C2_4_Servlet_that_counts_requests_without_the_necessary_synchronization;

import java.math.BigInteger;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.NotThreadSafe;

/**
 * UnsafeCountingFactorizer
 *
 * Servlet that counts requests without the necessary synchronization
 *
 * @author Brian Goetz and Tim Peierls
 */
@SuppressWarnings("serial")
@NotThreadSafe
public class UnsafeCountingFactorizer extends GenericServlet implements Servlet {

    private long count = 0;

    public long getCount() {
        return count;
    }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        BigInteger[] factors = factor( i );
        ++count;
        encodeIntoResponse( resp, factors );
    }

    void encodeIntoResponse(ServletResponse res, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return new BigInteger( "7" );
    }

    BigInteger[] factor(BigInteger i) {
        // Doesn't really factor
        return new BigInteger[] { i };
    }
}
