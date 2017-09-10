package C8_Applying_Thread_Pools.Ch8_15_Sequential_puzzle_solver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import C8_Applying_Thread_Pools.Ch8_13_Abstraction_for_puzzles_like_the_sliding_blocks_puzzle.Puzzle;
import C8_Applying_Thread_Pools.Ch8_14_Link_node_for_the_puzzle_solver_framework.PuzzleNode;

/**
 * SequentialPuzzleSolver
 * <p/>
 * Sequential puzzle solver
 *
 * @author Brian Goetz and Tim Peierls
 */

public class SequentialPuzzleSolver<P, M> {
    private final Puzzle< P, M > puzzle;
    private final Set< P > seen = new HashSet< P >();

    public SequentialPuzzleSolver(Puzzle< P, M > puzzle) {
        this.puzzle = puzzle;
    }

    public List< M > solve() {
        P pos = puzzle.initialPosition();
        return search( new PuzzleNode< P, M >( pos, null, null ) );
    }

    private List< M > search(PuzzleNode< P, M > node) {
        if (!seen.contains( node.getPos() )) {
            seen.add( node.getPos() );
            if (puzzle.isGoal( node.getPos() ))
                return node.asMoveList();
            for (M move : puzzle.legalMoves( node.getPos() )) {
                P pos = puzzle.move( node.getPos(), move );
                PuzzleNode< P, M > child = new PuzzleNode< P, M >( pos, move, node );
                List< M > result = search( child );
                if (result != null)
                    return result;
            }
        }
        return null;
    }
}
