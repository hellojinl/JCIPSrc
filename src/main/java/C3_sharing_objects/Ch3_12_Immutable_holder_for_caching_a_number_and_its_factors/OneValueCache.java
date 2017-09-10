package C3_sharing_objects.Ch3_12_Immutable_holder_for_caching_a_number_and_its_factors;

import java.math.BigInteger;
import java.util.Arrays;

import support.annotations.Immutable;

/**
 * OneValueCache
 * <p/>
 * Immutable holder for caching a number and its factors
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public class OneValueCache {
    private final BigInteger lastNumber;
    private final BigInteger[] lastFactors;

    public OneValueCache(BigInteger i, BigInteger[] factors) {
        lastNumber = i;
        lastFactors = Arrays.copyOf( factors, factors.length );
    }

    public BigInteger[] getFactors(BigInteger i) {
        if (lastNumber == null || !lastNumber.equals( i ))
            return null;
        else
            return Arrays.copyOf( lastFactors, lastFactors.length );
    }
}
