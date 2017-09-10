package Ch10_Avoiding_Liveness_Hazards.support;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class DollarAmount implements Comparable< DollarAmount > {

    private final int amount;

    public DollarAmount(int amount) {
        this.amount = amount;
    }

    public DollarAmount add(DollarAmount d) {
        return new DollarAmount( this.amount + d.amount );
    }

    public DollarAmount subtract(DollarAmount d) {
        return new DollarAmount( this.amount - d.amount );
    }

    public int compareTo(DollarAmount d) {
        return this.amount - d.amount;
    }
}
