package C2_threadsafety.C2_3_Race_condition_in_lazy_initialization;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * thread safe LazyInitRace
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see C2_threadsafety.C2_3_Race_condition_in_lazy_initialization.ExpensiveObject
 */
@ThreadSafe
public class SafeLazyInit {

    @GuardedBy("this")
    private ExpensiveObject instance = null;

    public synchronized ExpensiveObject getInstance() {
        if (instance == null)
            instance = new ExpensiveObject();
        return instance;
    }
}
