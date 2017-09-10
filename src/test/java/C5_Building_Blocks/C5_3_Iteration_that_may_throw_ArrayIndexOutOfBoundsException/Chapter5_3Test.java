package C5_Building_Blocks.C5_3_Iteration_that_may_throw_ArrayIndexOutOfBoundsException;

import static org.junit.Assert.assertTrue;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class Chapter5_3Test {

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

        assertTrue( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 为了测试效果
                        vector.clear();
                    }
                };
                t.start();

                try {
                    for (int i = 0; i < vector.size(); i++) {

                        Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS ); // 为了测试效果

                        doSomething( vector.get( i ) );
                    }
                    return false;
                } catch ( ArrayIndexOutOfBoundsException ex ) {
                    return true;
                }
            }
        } ) );

    }

    private <T> void doSomething(T data) {
        System.out.println( data );
    }
}
