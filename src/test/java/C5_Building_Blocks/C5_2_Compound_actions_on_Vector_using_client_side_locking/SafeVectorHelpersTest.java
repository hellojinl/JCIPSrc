package C5_Building_Blocks.C5_2_Compound_actions_on_Vector_using_client_side_locking;

import static org.junit.Assert.assertFalse;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class SafeVectorHelpersTest {

    final Vector< Integer > vector = new Vector< Integer >();

    @Before
    public void setUp() throws Exception {
        vector.clear();
        for (int i = 0; i < 100; i++) {
            vector.add( i );
        }
    }

    @Test
    public void test() {
        assertFalse( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                Thread t1 = new Thread() {
                    @Override
                    public void run() {
                        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
                        SafeVectorHelpers.deleteLast( vector ); // 试图破坏getLast操作的原子性
                    }
                };
                t1.start();

                try {
                    Integer lastElement = (Integer) SafeVectorHelpers.getLast( vector );
                    System.out.println( "lastElement = " + lastElement );
                    return false;
                } catch ( ArrayIndexOutOfBoundsException ex ) {
                    return true; // 当前场景，永远不会发生
                }
            }

            public int maximumExecutionTimes() {
                return 3;
            }

        } ) );

    }

    static class SafeVectorHelpers {

        public static Object getLast(Vector list) {
            synchronized ( list ) {
                int lastIndex = list.size() - 1;

                Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS ); // 为了测试效果

                return list.get( lastIndex );
            }
        }

        public static void deleteLast(Vector list) {
            synchronized ( list ) {
                int lastIndex = list.size() - 1;
                list.remove( lastIndex );
            }
        }
    }
}
