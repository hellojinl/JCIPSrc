package C8_Applying_Thread_Pools.Ch8_16_Concurrent_version_of_puzzle_solver;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import C8_Applying_Thread_Pools.Ch8_13_Abstraction_for_puzzles_like_the_sliding_blocks_puzzle.Puzzle;
import C8_Applying_Thread_Pools.Ch8_14_Link_node_for_the_puzzle_solver_framework.PuzzleNode;
import C8_Applying_Thread_Pools.Ch8_17_Result_bearing_latch_used_by_ConcurrentPuzzleSolver.ValueLatch;

/**
 * ConcurrentPuzzleSolver
 * <p/>
 * Concurrent version of puzzle solver
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle< P, M > puzzle;
    private final ExecutorService exec;
    private final ConcurrentMap< P, Boolean > seen;
    protected final ValueLatch< PuzzleNode< P, M > > solution = new ValueLatch< PuzzleNode< P, M > >();

    public ConcurrentPuzzleSolver(Puzzle< P, M > puzzle) {
        this.puzzle = puzzle;
        this.exec = initThreadPool();
        this.seen = new ConcurrentHashMap< P, Boolean >();
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) exec;
            tpe.setRejectedExecutionHandler( new ThreadPoolExecutor.DiscardPolicy() );
        }
    }

    private ExecutorService initThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public List< M > solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute( newTask( p, null, null ) );
            // block until solution found
            PuzzleNode< P, M > solnPuzzleNode = solution.getValue();
            return (solnPuzzleNode == null) ? null : solnPuzzleNode.asMoveList();
        } finally {
            exec.shutdown();
        }
    }

    protected Runnable newTask(P p, M m, PuzzleNode< P, M > n) {
        return new SolverTask( p, m, n );
    }

    protected class SolverTask extends PuzzleNode< P, M > implements Runnable {
        protected SolverTask(P pos, M move, PuzzleNode< P, M > prev) {
            super( pos, move, prev );
        }

        public void run() {
            if (solution.isSet() || seen.putIfAbsent( this.getPos(), true ) != null)
                return; // already solved or seen this position
            if (puzzle.isGoal( this.getPos() ))
                solution.setValue( this );
            else
                for (M m : puzzle.legalMoves( this.getPos() ))
                    exec.execute( newTask( puzzle.move( this.getPos(), m ), m, this ) );
        }
    }
}
