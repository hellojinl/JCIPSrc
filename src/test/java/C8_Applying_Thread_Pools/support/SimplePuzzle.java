package C8_Applying_Thread_Pools.support;

import java.util.HashSet;
import java.util.Set;

import C8_Applying_Thread_Pools.Ch8_13_Abstraction_for_puzzles_like_the_sliding_blocks_puzzle.Puzzle;
import support.RandomUtil;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SimplePuzzle implements Puzzle< PuzzlePoint, Move< PuzzlePoint > > {

    private PuzzlePoint goal = new PuzzlePoint( PuzzlePoint.MIN_X, PuzzlePoint.MIN_Y );

    private PuzzlePoint startPoint;

    @Override
    public PuzzlePoint initialPosition() {
        int x = RandomUtil.get( PuzzlePoint.MIN_X, PuzzlePoint.MAX_X );
        int y = RandomUtil.get( PuzzlePoint.MIN_Y, PuzzlePoint.MAX_Y );

        synchronized ( this ) {
            startPoint = new PuzzlePoint( x, y );
            return startPoint;
        }
    }

    public PuzzlePoint getStartPoint() {
        synchronized ( this ) {
            return startPoint;
        }
    }

    public void setGoal(PuzzlePoint goal) {
        this.goal = goal;
    }

    @Override
    public boolean isGoal(PuzzlePoint position) {
        return goal.equals( position );
    }

    @Override
    public Set< Move< PuzzlePoint > > legalMoves(PuzzlePoint position) {
        Set< Move< PuzzlePoint > > moves = new HashSet<>();

        MoveUp up = new MoveUp();
        if (up.attempt( position )) {
            moves.add( up );
        }

        MoveRight right = new MoveRight();
        if (right.attempt( position )) {
            moves.add( right );
        }

        MoveDown down = new MoveDown();
        if (down.attempt( position )) {
            moves.add( down );
        }

        MoveLeft left = new MoveLeft();
        if (left.attempt( position )) {
            moves.add( left );
        }

        return moves;
    }

    @Override
    public PuzzlePoint move(PuzzlePoint position, Move< PuzzlePoint > move) {
        return move.moveTo( position );
    }

}
