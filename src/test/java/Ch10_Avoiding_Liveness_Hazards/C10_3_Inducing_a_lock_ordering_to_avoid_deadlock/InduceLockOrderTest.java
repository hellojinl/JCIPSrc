package Ch10_Avoiding_Liveness_Hazards.C10_3_Inducing_a_lock_ordering_to_avoid_deadlock;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Ch10_Avoiding_Liveness_Hazards.support.Account;
import Ch10_Avoiding_Liveness_Hazards.support.DollarAmount;
import Ch10_Avoiding_Liveness_Hazards.support.InsufficientFundsException;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class InduceLockOrderTest {

    final DollarAmount $10000 = new DollarAmount( 10000 );
    final DollarAmount $8000 = new DollarAmount( 8000 );
    final DollarAmount $5000 = new DollarAmount( 5000 );
    final DollarAmount $3000 = new DollarAmount( 3000 );

    @Test
    public void test() throws InterruptedException {
        final Account a = new Account( $10000 );
        final Account b = new Account( $8000 );

        final InduceLockOrder order = new InduceLockOrder();

        Thread a2b = new Thread( () -> {
            try {
                order.transferMoney( a, b, $5000 );
            } catch ( InsufficientFundsException e ) {
                e.printStackTrace();
            }
        } );

        Thread b2a = new Thread( () -> {
            try {
                order.transferMoney( b, a, $3000 );
            } catch ( InsufficientFundsException e ) {
                e.printStackTrace();
            }
        } );

        a2b.start();
        b2a.start();

        a2b.join();
        b2a.join();

        assertTrue( a.getBalance().compareTo( $8000 ) == 0 );
        assertTrue( b.getBalance().compareTo( $10000 ) == 0 );

    }

    static class InduceLockOrder {
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
}
