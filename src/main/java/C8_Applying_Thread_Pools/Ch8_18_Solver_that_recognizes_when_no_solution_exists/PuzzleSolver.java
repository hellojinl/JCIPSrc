package C8_Applying_Thread_Pools.Ch8_18_Solver_that_recognizes_when_no_solution_exists;

import java.util.concurrent.atomic.AtomicInteger;

import C8_Applying_Thread_Pools.Ch8_13_Abstraction_for_puzzles_like_the_sliding_blocks_puzzle.Puzzle;
import C8_Applying_Thread_Pools.Ch8_14_Link_node_for_the_puzzle_solver_framework.PuzzleNode;
import C8_Applying_Thread_Pools.Ch8_16_Concurrent_version_of_puzzle_solver.ConcurrentPuzzleSolver;

/**
 * PuzzleSolver
 * <p/>
 * Solver that recognizes when no solution exists
 *
 * @author Brian Goetz and Tim Peierls
 */
public class PuzzleSolver<P, M> extends ConcurrentPuzzleSolver< P, M > {
    PuzzleSolver(Puzzle< P, M > puzzle) {
        super( puzzle );
    }

    private final AtomicInteger taskCount = new AtomicInteger( 0 );

    protected Runnable newTask(P p, M m, PuzzleNode< P, M > n) {
        return new CountingSolverTask( p, m, n );
    }

    class CountingSolverTask extends SolverTask {
        CountingSolverTask(P pos, M move, PuzzleNode< P, M > prev) {
            super( pos, move, prev );
            taskCount.incrementAndGet();
        }

        public void run() {
            try {
                super.run();
            } finally {
                if (taskCount.decrementAndGet() == 0)
                    solution.setValue( null );
            }
        }
    }
}
