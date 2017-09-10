package C8_Applying_Thread_Pools.Ch8_18_Solver_that_recognizes_when_no_solution_exists;

import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import C8_Applying_Thread_Pools.support.Move;
import C8_Applying_Thread_Pools.support.PuzzlePoint;
import C8_Applying_Thread_Pools.support.SimplePuzzle;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class PuzzleSolverTest {

    @Test
    public void test() throws InterruptedException {
        SimplePuzzle puzzle = new SimplePuzzle();
        puzzle.setGoal( PuzzlePoint.ILLEGAL_POINT ); // 设置一个永远无法达到的点

        PuzzleSolver< PuzzlePoint, Move< PuzzlePoint > > solver = new PuzzleSolver< PuzzlePoint, Move< PuzzlePoint > >(
                puzzle );
        List< Move< PuzzlePoint > > moves = solver.solve();
        assertNull( moves );
    }

    // 以我的实现为依据（矩形边界），对PuzzleSolver做个简单说明。
    // 一个task表示对一个解决路径上一个位置的探索，当走向下一个位置（最多可同时走向4个方向，也可能无路可走）时，当前task就结束了
    // 什么情况下task数量会上升? 当下一个可能的位置个数大于1时，task个数将增加
    // 什么情况下task数量会下降? 当不存在可能位置时，task个数将减少
    // 那么在这里，目标是一个无法到达的位置，而探索区域又是有边界的，那么在若干步之后，所有的线程都将无路可走，它们全部会消失。
    // 这时task的个数将为0，（但凡存在一种可能性，task的个数必然＞0），所以当task的个数为0时，则认为该任务是不可完成的。
}
