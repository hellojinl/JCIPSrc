package Ch10_Avoiding_Liveness_Hazards.C10_2_Dynamic_lock_ordering_deadlock;

import Ch10_Avoiding_Liveness_Hazards.support.Account;
import Ch10_Avoiding_Liveness_Hazards.support.DollarAmount;
import Ch10_Avoiding_Liveness_Hazards.support.InsufficientFundsException;
import support.annotations.Evaluated;

/**
 * DynamicOrderDeadlock
 * <p/>
 * Dynamic lock-ordering deadlock
 *
 * @author Brian Goetz and Tim Peierls
 */
@Evaluated(">_<")
public class DynamicOrderDeadlock {
    // Warning: deadlock-prone!
    public static void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount)
            throws InsufficientFundsException {
        synchronized ( fromAccount ) {
            synchronized ( toAccount ) {
                if (fromAccount.getBalance().compareTo( amount ) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAccount.debit( amount );
                    toAccount.credit( amount );
                }
            }
        }
    }

}
