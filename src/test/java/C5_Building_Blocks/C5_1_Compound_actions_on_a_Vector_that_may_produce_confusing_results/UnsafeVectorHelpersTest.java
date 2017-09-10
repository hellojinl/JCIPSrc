package C5_Building_Blocks.C5_1_Compound_actions_on_a_Vector_that_may_produce_confusing_results;

import static org.junit.Assert.assertTrue;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class UnsafeVectorHelpersTest {

    final Vector< Integer > vector = new Vector< Integer >();

    @Before
    public void setUp() throws Exception {
        vector.clear();
        for (int i = 0; i < 100; i++) {
            vector.add( i );
        }
    }

    @Test
    public void test_getLast() {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );// 为了测试效果
                        UnsafeVectorHelpers.deleteLast( vector ); // 破坏getLast操作，让它抛出异常
                    }
                };
                t.start();

                try {
                    Integer lastElement = (Integer) UnsafeVectorHelpers.getLast( vector );
                    System.out.println( "lastElement = " + lastElement );
                    return false;
                } catch ( ArrayIndexOutOfBoundsException ex ) {
                    return true; // 抛出异常，期待的结果
                }
            }

        } ) );

    }

    static class UnsafeVectorHelpers {
        public static Object getLast(Vector list) {
            int lastIndex = list.size() - 1;

            Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS ); // 为了测试效果

            return list.get( lastIndex );
        }

        public static void deleteLast(Vector list) {
            int lastIndex = list.size() - 1;
            list.remove( lastIndex );
        }
    }
}
