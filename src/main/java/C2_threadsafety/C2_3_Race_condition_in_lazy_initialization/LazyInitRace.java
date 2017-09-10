package C2_threadsafety.C2_3_Race_condition_in_lazy_initialization;

import support.annotations.NotThreadSafe;

/**
 * LazyInitRace
 *
 * Race condition in lazy initialization
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class LazyInitRace {

    private ExpensiveObject instance = null;

    public ExpensiveObject getInstance() {
        if (instance == null)
            instance = new ExpensiveObject();
        return instance;
    }
}

class ExpensiveObject {
}
