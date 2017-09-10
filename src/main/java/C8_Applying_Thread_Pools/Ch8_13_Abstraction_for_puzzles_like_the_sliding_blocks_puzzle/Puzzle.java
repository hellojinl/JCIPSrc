package C8_Applying_Thread_Pools.Ch8_13_Abstraction_for_puzzles_like_the_sliding_blocks_puzzle;

import java.util.Set;

/**
 * Puzzle
 * <p/>
 * Abstraction for puzzles like the 'sliding blocks puzzle'
 *
 * @author Brian Goetz and Tim Peierls
 */
public interface Puzzle<P, M> {
    P initialPosition();

    boolean isGoal(P position);

    Set< M > legalMoves(P position);

    P move(P position, M move);
}
