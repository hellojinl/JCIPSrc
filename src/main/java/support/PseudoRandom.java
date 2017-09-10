package support;

/**
 * PseudoRandom
 *
 * @author Brian Goetz and Tim Peierls
 */
public class PseudoRandom {

    protected int calculateNext(int prev) {
        prev ^= prev << 6;
        prev ^= prev >>> 21;
        prev ^= (prev << 7);
        return prev;
    }
}