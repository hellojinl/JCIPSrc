package C3_sharing_objects.Ch3_13_Caching_the_last_result_using_a_volatile_reference_to_an_immutable_holder_object;

import java.math.BigInteger;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import support.annotations.ThreadSafe;
import C3_sharing_objects.Ch3_12_Immutable_holder_for_caching_a_number_and_its_factors.OneValueCache;

/**
 * VolatileCachedFactorizer
 * <p/>
 * Caching the last result using a volatile reference to an immutable holder
 * object
 *
 * @author Brian Goetz and Tim Peierls
 */
@SuppressWarnings("serial")
@ThreadSafe
public class VolatileCachedFactorizer extends GenericServlet implements Servlet {
    private volatile OneValueCache cache = new OneValueCache( null, null );

    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = extractFromRequest( req );
        BigInteger[] factors = cache.getFactors( i );
        if (factors == null) {
            factors = factor( i );
            // 因为OneValueCache是不可变量，所以没有任何线程能修改它，它的状态将永远一致。
            // 当用另个一个OneValueCache来更新cache时，cache从一个一致性状态转换到另一个一致性状态，永远处于一致性状态
            // 此外，如果cache没有用volatile修饰，那么由于可见性问题，其他线程看到的cache将不一致（虽然在各个线程中各自看到的cache状态是一致的，但是综合起来在多个线程中可能出现不一致），
            // 所以，为了保证多线程的一致性（或者说，cache对其他线程的可见性），cache还必须用volatile修饰
            cache = new OneValueCache( i, factors );

        }
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
