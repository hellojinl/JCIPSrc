package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_2_Nonblocking_counter_using_CAS;

import Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_1_Simulated_CAS_operation.SimulatedCAS;
import support.annotations.ThreadSafe;

/**
 * CasCounter
 * <p/>
 * Nonblocking counter using CAS
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public int getValue() {
        return value.get();
    }

    public int increment() {
        int v;
        do {
            v = value.get();
        } while ( v != value.compareAndSwap( v, v + 1 ) );
        return v + 1;
    }
}
