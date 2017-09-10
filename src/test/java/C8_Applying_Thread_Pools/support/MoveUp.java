package C8_Applying_Thread_Pools.support;

/**
 * 向上
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class MoveUp implements Move< PuzzlePoint > {

    private PuzzlePoint destination;

    @Override
    public PuzzlePoint moveTo(PuzzlePoint from) {
        if (from == null) {
            throw new NullPointerException();
        }
        int y = from.getY() + 1;
        if (y > PuzzlePoint.MAX_Y) {
            return PuzzlePoint.ILLEGAL_POINT;
        }

        synchronized ( this ) {
            destination = new PuzzlePoint( from.getX(), y );
            return destination;
        }
    }

    @Override
    public String description() {
        return "->up->" + getDestination();
    }

    @Override
    public PuzzlePoint getDestination() {
        synchronized ( this ) {
            return destination;
        }
    }
}
