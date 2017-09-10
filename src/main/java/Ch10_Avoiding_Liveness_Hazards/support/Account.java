package Ch10_Avoiding_Liveness_Hazards.support;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class Account {
    private DollarAmount balance;
    private final int acctNo;
    private static final AtomicInteger sequence = new AtomicInteger();

    public Account() {
        acctNo = sequence.incrementAndGet();
    }

    public Account(DollarAmount b) {
        this();
        this.balance = b;
    }

    public void debit(DollarAmount d) {
        balance = balance.subtract( d );
    }

    public void credit(DollarAmount d) {
        balance = balance.add( d );
    }

    public DollarAmount getBalance() {
        return balance;
    }

    public int getAcctNo() {
        return acctNo;
    }
}
