package C8_Applying_Thread_Pools.Ch8_16_Concurrent_version_of_puzzle_solver;

import java.util.List;

import org.junit.Test;

import C8_Applying_Thread_Pools.support.Move;
import C8_Applying_Thread_Pools.support.PuzzlePoint;
import C8_Applying_Thread_Pools.support.SimplePuzzle;
import testUtils.Timer;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ConcurrentPuzzleSolverTest {

    @Test
    public void runIt() throws InterruptedException {
        SimplePuzzle puzzle = new SimplePuzzle();
        ConcurrentPuzzleSolver< PuzzlePoint, Move< PuzzlePoint > > solver = new ConcurrentPuzzleSolver<>( puzzle );

        long millis = Timer.timeMillis( () -> {
            List< Move< PuzzlePoint > > moves = solver.solve();
            System.out.print( puzzle.getStartPoint() );
            moves.forEach( m -> {
                System.out.print( m.description() );
            } );
        } );
        System.out.println();
        System.out.println( "ConcurrentPuzzleSolver spent " + millis + " millis" );

        // 在这里由于每一步消耗的时间非常少，因此还不足以体现并发优势，实际的测试结果也是如此。
        // 但是当每一步消耗的时间到达一定量时，并发的效率将显著的大于串行
        // ^^如果能确保每一步都消耗非常少的时间，那么串行比并行更好（资源更少，速度更快），
        // 比如，在并发还在创建线程的时候，串行就已经把一个任务完成了。
    }
}
