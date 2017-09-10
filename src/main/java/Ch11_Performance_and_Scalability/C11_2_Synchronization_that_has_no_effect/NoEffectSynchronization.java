package Ch11_Performance_and_Scalability.C11_2_Synchronization_that_has_no_effect;

import support.annotations.Evaluated;
import support.annotations.NotThreadSafe;

/**
 * 没有作用的同步
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@NotThreadSafe
public class NoEffectSynchronization {

    private int count;

    @Evaluated(">_<")
    public void increase() {
        synchronized ( new Object() ) { // no effect
            count++;
        }
    }

    @Evaluated(">_<")
    public int get() {
        synchronized ( new Object() ) { // no effect
            return count;
        }
    }

}
