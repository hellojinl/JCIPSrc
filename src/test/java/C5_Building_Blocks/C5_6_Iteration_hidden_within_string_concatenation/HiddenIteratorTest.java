package C5_Building_Blocks.C5_6_Iteration_hidden_within_string_concatenation;

import static org.junit.Assert.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.annotations.GuardedBy;
import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class HiddenIteratorTest {

    final HiddenIterator hiddenIterator = new HiddenIterator();

    @Test
    public void test() {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            public void setUp() throws Exception {
                for (int i = 0; i < 5; i++) {
                    hiddenIterator.add( i );
                }
            }

            public void tearDown() throws Exception {
                hiddenIterator.set.clear();
            }

            @Override
            public boolean doConcurrentTest() throws Exception {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 100; i++) {
                            Sleep.sleepUninterruptibly( 1, TimeUnit.MILLISECONDS );
                            hiddenIterator.add( i );
                        }
                    }
                };
                t.start();

                try {
                    hiddenIterator.addTenThings();
                    return false;
                } catch ( ConcurrentModificationException ex ) {
                    return true;
                } finally {
                    t.join();
                }
            }

        } ) );

    }

    class HiddenIterator {
        @GuardedBy("this")
        private final Set< Integer > set = new SlowHashSet< Integer >();

        public synchronized void add(Integer i) {
            set.add( i );
        }

        public synchronized void remove(Integer i) {
            set.remove( i );
        }

        public void addTenThings() {
            Random r = new Random();
            for (int i = 0; i < 10; i++)
                add( r.nextInt() );

            // 下面这句话隐含地调用了迭代器，see AbstractCollection
            // 有可能抛出 ConcurrentModificationException
            System.out.println( "DEBUG: added ten elements to " + set );
        }
    }

    class SlowHashSet<E> extends HashSet< E > {

        private static final long serialVersionUID = -3067850443365850671L;

        @Override
        public String toString() {
            Iterator< E > it = iterator();
            if (!it.hasNext())
                return "[]";

            StringBuilder sb = new StringBuilder();
            sb.append( '[' );
            for (;;) {
                Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS ); // 为了测试效果，对toString方法减速

                E e = it.next();
                sb.append( e == this ? "(this Collection)" : e );
                if (!it.hasNext())
                    return sb.append( ']' ).toString();
                sb.append( ',' ).append( ' ' );
            }
        }
    }

}
