package Ch10_Avoiding_Liveness_Hazards.C10_1_Simple_lock_ordering_deadlock;

import support.annotations.Evaluated;

/**
 * LeftRightDeadlock
 *
 * Simple lock-ordering deadlock
 *
 * @author Brian Goetz and Tim Peierls
 */
@Evaluated(">_<")
public class LeftRightDeadlock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized ( left ) {
            synchronized ( right ) {
                doSomething();
            }
        }
    }

    public void rightLeft() {
        synchronized ( right ) {
            synchronized ( left ) {
                doSomethingElse();
            }
        }
    }

    void doSomething() {
    }

    void doSomethingElse() {
    }
}
