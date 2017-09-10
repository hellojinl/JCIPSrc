package C8_Applying_Thread_Pools.support;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class PuzzlePoint {

    public final static int MIN_X = 0;
    public final static int MIN_Y = 0;
    public final static int MAX_X = 10;
    public final static int MAX_Y = 10;

    public final static PuzzlePoint ILLEGAL_POINT = new PuzzlePoint( -1, -1 );

    private final int x;

    private final int y;

    PuzzlePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PuzzlePoint) {
            PuzzlePoint other = (PuzzlePoint) obj;
            return this.getX() == other.getX() && this.getY() == other.getY();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        return hash;
    }

    @Override
    public String toString() {
        return String.format( "(%d, %d)", this.getX(), this.getY() );
    }
}
