package Ch10_Avoiding_Liveness_Hazards.C10_2_Dynamic_lock_ordering_deadlock;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import Ch10_Avoiding_Liveness_Hazards.support.Account;
import Ch10_Avoiding_Liveness_Hazards.support.DollarAmount;
import Ch10_Avoiding_Liveness_Hazards.support.InsufficientFundsException;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class DynamicOrderDeadlockTest {

    final DollarAmount $10000 = new DollarAmount( 10000 );
    final DollarAmount $8000 = new DollarAmount( 8000 );
    final DollarAmount $5000 = new DollarAmount( 5000 );
    final DollarAmount $3000 = new DollarAmount( 3000 );

    @Test
    public void test() {

        final Account a = new Account( $10000 );
        final Account b = new Account( $8000 );

        Thread a2b = new Thread( () -> {
            try {
                DynamicOrderDeadlock.transferMoney( a, b, $5000 );
            } catch ( InsufficientFundsException e ) {
                e.printStackTrace();
            }
        } );

        Thread b2a = new Thread( () -> {
            try {
                DynamicOrderDeadlock.transferMoney( b, a, $3000 );
            } catch ( InsufficientFundsException e ) {
                e.printStackTrace();
            }
        } );

        a2b.start();
        b2a.start();

        // 执行足够的时间
        Sleep.sleepUninterruptibly( 4, TimeUnit.SECONDS );

        // 检测死锁，资金没有变化
        assertTrue( a.getBalance().compareTo( $10000 ) == 0 );
        assertTrue( b.getBalance().compareTo( $8000 ) == 0 );

    }

    static class DynamicOrderDeadlock {
        // Warning: deadlock-prone!
        public static void transferMoney(Account fromAccount, Account toAccount, DollarAmount amount)
                throws InsufficientFundsException {
            synchronized ( fromAccount ) {
                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
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
}
