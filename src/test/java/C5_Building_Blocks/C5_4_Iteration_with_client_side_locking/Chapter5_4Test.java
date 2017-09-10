package C5_Building_Blocks.C5_4_Iteration_with_client_side_locking;

import static org.junit.Assert.assertFalse;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class Chapter5_4Test {

    final Vector< Integer > vector = new Vector< Integer >();

    @Test
    public void test() {

        assertFalse( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public void setUp() throws Exception {
                vector.clear();
                for (int i = 0; i < 2; i++) {
                    vector.add( i );
                }
            }

            @Override
            public boolean doConcurrentTest() throws Exception {

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS ); // 为了测试效果

                        vector.clear();
                    }
                };
                t.start();

                try {
                    synchronized ( vector ) {
                        for (int i = 0; i < vector.size(); i++) {

                            try {
                                TimeUnit.SECONDS.sleep( 2 ); // 为了测试效果
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();
                            }

                            doSomething( vector.get( i ) );
                        }
                    }

                    return false;
                } catch ( ArrayIndexOutOfBoundsException ex ) {
                    ex.printStackTrace();
                    return true;
                } finally {
                    t.join();
                }
            }

            @Override
            public int maximumExecutionTimes() {
                return 3;
            }

        } ) );

    }

    private <T> void doSomething(T data) {
        System.out.println( data );
    }
}
