package C5_Building_Blocks.C5_3_Iteration_that_may_throw_ArrayIndexOutOfBoundsException;

import java.util.Vector;

/**
 * vector是线程安全的，但是它的复合操作不一定是线程安全的
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see C5_Building_Blocks.C5_3_Iteration_that_may_throw_ArrayIndexOutOfBoundsException.Chapter5_3Test
 */
public class Chapter5_3 {

    public final Vector< Integer > vector = new Vector< Integer >();

    /**
     * vector是线程安全的，但是fragment代码的正确性依赖于运气
     */
    public void fragment() {
        for (int i = 0; i < vector.size(); i++) {
            doSomething( vector.get( i ) );
        }
    }

    private void doSomething(Object obj) {

    }

}
