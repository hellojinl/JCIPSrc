package C8_Applying_Thread_Pools.Ch8_15_Sequential_puzzle_solver;

import java.util.List;

import org.junit.Test;

import C8_Applying_Thread_Pools.support.Move;
import C8_Applying_Thread_Pools.support.PuzzlePoint;
import C8_Applying_Thread_Pools.support.SimplePuzzle;
import testUtils.Timer;

/**
 * 一个简单的串行迷宫解答器实现。
 * <ul>
 * <li>这里的点为二维点(x, y)</li>
 * <li>移动只有4种（上，右，下，左）</li>
 * <li>终点是(0, 0)</li>
 * 
 * </ul>
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SequentialPuzzleSolverTest {

    /**
     * 某次运行结果 (0, 9)->down->(0, 8)->down->(0, 7)->right->(1, 7)->right->(2,
     * 7)->right->(3, 7)->up->(3, 8)->right->(4, 8)->up->(4, 9)->left->(3,
     * 9)->left->(2, 9)->left->(1, 9)->up->(1, 10)->right->(2, 10)->right->(3,
     * 10)->right->(4, 10)->right->(5, 10)->down->(5, 9)->right->(6,
     * 9)->down->(6, 8)->right->(7, 8)->up->(7, 9)->up->(7, 10)->right->(8,
     * 10)->down->(8, 9)->down->(8, 8)->down->(8, 7)->left->(7, 7)->down->(7,
     * 6)->right->(8, 6)->right->(9, 6)->right->(10, 6)->down->(10,
     * 5)->left->(9, 5)->down->(9, 4)->left->(8, 4)->down->(8, 3)->down->(8,
     * 2)->right->(9, 2)->right->(10, 2)->down->(10, 1)->down->(10,
     * 0)->left->(9, 0)->left->(8, 0)->up->(8, 1)->left->(7, 1)->down->(7,
     * 0)->left->(6, 0)->up->(6, 1)->up->(6, 2)->left->(5, 2)->up->(5,
     * 3)->left->(4, 3)->down->(4, 2)->down->(4, 1)->left->(3, 1)->down->(3,
     * 0)->left->(2, 0)->left->(1, 0)->left->(0, 0)
     */
    @Test
    public void runIt() {
        SimplePuzzle puzzle = new SimplePuzzle();
        SequentialPuzzleSolver< PuzzlePoint, Move< PuzzlePoint > > solver = new SequentialPuzzleSolver<>( puzzle );

        long millis = Timer.timeMillis( () -> {
            List< Move< PuzzlePoint > > moves = solver.solve();

            // (-_-) 仔细看一遍结果，会觉得这个算法很傻，改成并发之后还是特别傻，唯一的用途就是满足了演示要求
            System.out.print( puzzle.getStartPoint() );
            moves.forEach( m -> {
                System.out.print( m.description() );
            } );
        } );

        System.out.println();
        System.out.println( "SequentialPuzzleSolver spent " + millis + " millis" );
    }
}
