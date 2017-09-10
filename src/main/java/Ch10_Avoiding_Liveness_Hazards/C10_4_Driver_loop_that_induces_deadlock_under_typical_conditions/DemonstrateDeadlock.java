package Ch10_Avoiding_Liveness_Hazards.C10_4_Driver_loop_that_induces_deadlock_under_typical_conditions;

import java.util.Random;

import Ch10_Avoiding_Liveness_Hazards.C10_2_Dynamic_lock_ordering_deadlock.DynamicOrderDeadlock;
import Ch10_Avoiding_Liveness_Hazards.support.Account;
import Ch10_Avoiding_Liveness_Hazards.support.DollarAmount;
import Ch10_Avoiding_Liveness_Hazards.support.InsufficientFundsException;

/**
 * DemonstrateDeadlock
 * <p/>
 * Driver loop that induces deadlock under typical conditions
 *
 * @author Brian Goetz and Tim Peierls
 */
public class DemonstrateDeadlock {
    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    public static void main(String[] args) {
        final Random rnd = new Random();
        final Account[] accounts = new Account[ NUM_ACCOUNTS ];

        for (int i = 0; i < accounts.length; i++)
            accounts[i] = new Account();

        class TransferThread extends Thread {
            public void run() {
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    int fromAcct = rnd.nextInt( NUM_ACCOUNTS );
                    int toAcct = rnd.nextInt( NUM_ACCOUNTS );
                    DollarAmount amount = new DollarAmount( rnd.nextInt( 1000 ) );
                    try {
                        DynamicOrderDeadlock.transferMoney( accounts[fromAcct], accounts[toAcct], amount );
                    } catch ( InsufficientFundsException ignored ) {
                    }
                }
            }
        }
        for (int i = 0; i < NUM_THREADS; i++)
            new TransferThread().start();
    }
}
