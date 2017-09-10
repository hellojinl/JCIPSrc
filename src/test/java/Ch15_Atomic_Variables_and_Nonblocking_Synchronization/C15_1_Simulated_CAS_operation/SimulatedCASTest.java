package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_1_Simulated_CAS_operation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SimulatedCASTest {

    @Test
    public void test() {
        SimulatedCAS cas = new SimulatedCAS();

        int expectedValue = cas.get();
        assertTrue( cas.compareAndSet( expectedValue, 10 ) );

        assertFalse( cas.compareAndSet( expectedValue, 100 ) );

        expectedValue = cas.get();
        assertTrue( cas.compareAndSet( expectedValue, 100 ) );

    }
}
