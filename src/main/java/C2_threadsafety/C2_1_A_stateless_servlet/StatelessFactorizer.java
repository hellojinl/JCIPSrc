package C2_threadsafety.C2_1_A_stateless_servlet;

import java.math.BigInteger;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.ThreadSafe;

/**
 * StatelessFactorizer
 *
 * A stateless servlet
 * 
 * @author Brian Goetz and Tim Peierls
 */
@SuppressWarnings("serial")
@ThreadSafe
public class StatelessFactorizer extends GenericServlet implements Servlet {

    @Override
    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        BigInteger[] factors = factor( i );
        encodeIntoResponse( resp, factors );
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
