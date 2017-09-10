package Ch10_Avoiding_Liveness_Hazards.C10_3_Inducing_a_lock_ordering_to_avoid_deadlock;

import Ch10_Avoiding_Liveness_Hazards.support.Account;
import Ch10_Avoiding_Liveness_Hazards.support.DollarAmount;
import Ch10_Avoiding_Liveness_Hazards.support.InsufficientFundsException;

/**
 * InduceLockOrder
 *
 * Inducing a lock order to avoid deadlock
 *
 * @author Brian Goetz and Tim Peierls
 */
public class InduceLockOrder {
    private static final Object tieLock = new Object();

    public void transferMoney(final Account fromAcct, final Account toAcct, final DollarAmount amount)
            throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAcct.getBalance().compareTo( amount ) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAcct.debit( amount );
                    toAcct.credit( amount );
                }
            }
        }
        int fromHash = System.identityHashCode( fromAcct );
        int toHash = System.identityHashCode( toAcct );

        if (fromHash < toHash) {
            synchronized ( fromAcct ) {
                synchronized ( toAcct ) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized ( toAcct ) {
                synchronized ( fromAcct ) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized ( tieLock ) {
                synchronized ( fromAcct ) {
                    synchronized ( toAcct ) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }

}
