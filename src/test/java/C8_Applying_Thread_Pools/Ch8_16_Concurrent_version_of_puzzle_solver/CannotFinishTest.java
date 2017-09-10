package C8_Applying_Thread_Pools.Ch8_16_Concurrent_version_of_puzzle_solver;

import static org.junit.Assert.fail;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C8_Applying_Thread_Pools.support.Move;
import C8_Applying_Thread_Pools.support.PuzzlePoint;
import C8_Applying_Thread_Pools.support.SimplePuzzle;

/**
 * 测试目标不可能到达时会如何 ConcurrentPuzzleSolver并不能很好的应对任务无法完成的情况
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CannotFinishTest {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool( 1 );

    @Test
    public void test() throws InterruptedException {
        SimplePuzzle puzzle = new SimplePuzzle();
        puzzle.setGoal( PuzzlePoint.ILLEGAL_POINT ); // 设置一个永远无法达到的点

        final Thread taskThread = Thread.currentThread();
        cancelExec.schedule( new Runnable() {
            public void run() {
                taskThread.interrupt();
            }
        }, 5, TimeUnit.SECONDS ); // 如果能结束，一般不超过100毫秒，这里设置的比较大，如果5秒内完不成任务就认为任务不可完成。

        try {
            ConcurrentPuzzleSolver< PuzzlePoint, Move< PuzzlePoint > > solver = new ConcurrentPuzzleSolver<>( puzzle );
            solver.solve();
            fail( "cannot finish" );
        } catch ( InterruptedException ex ) {
            ex.printStackTrace();
        }
    }
}
