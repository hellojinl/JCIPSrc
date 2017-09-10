package C8_Applying_Thread_Pools.Ch8_14_Link_node_for_the_puzzle_solver_framework;

import java.util.LinkedList;
import java.util.List;

import support.annotations.Immutable;

/**
 * PuzzleNode
 * <p/>
 * Link node for the puzzle solving framework
 *
 * @author Brian Goetz and Tim Peierls
 */
@Immutable
public class PuzzleNode<P, M> {
    private final P pos;
    private final M move;
    private final PuzzleNode< P, M > prev;

    public PuzzleNode(P pos, M move, PuzzleNode< P, M > prev) {
        this.pos = pos;
        this.move = move;
        this.prev = prev;
    }

    public List< M > asMoveList() {
        List< M > solution = new LinkedList< M >();
        for (PuzzleNode< P, M > n = this; n.move != null; n = n.prev)
            solution.add( 0, n.move );
        return solution;
    }

    public P getPos() {
        return pos;
    }

    public M getMove() {
        return move;
    }

    public PuzzleNode< P, M > getPrev() {
        return prev;
    }
}
