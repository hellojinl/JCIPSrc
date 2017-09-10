package C2_threadsafety.C2_2_Servlet_that_counts_requests_using_AtomicLong;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.ThreadSafe;

/**
 * CountingFactorizer
 *
 * Servlet that counts requests using AtomicLong
 *
 * @author Brian Goetz and Tim Peierls
 */
@SuppressWarnings("serial")
@ThreadSafe
public class CountingFactorizer extends GenericServlet implements Servlet {

    private final AtomicLong count = new AtomicLong( 0 );

    public long getCount() {
        return count.get();
    }

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        BigInteger[] factors = factor( i );
        count.incrementAndGet();
        encodeIntoResponse( resp, factors );
    }

    void encodeIntoResponse(ServletResponse res, BigInteger[] factors) {
    }

    BigInteger extractFromRequest(ServletRequest req) {
        return null;
    }

    BigInteger[] factor(BigInteger i) {
        return null;
    }
}
