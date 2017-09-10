package C4_composing_objects.Ch4_14_Non_thread_safe_attempt_to_implement_put_if_absent;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.annotations.NotThreadSafe;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class BadListHelperTest {

    @Test
    public void test() throws InterruptedException {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                final Person tom = new Person( "tom" );
                final BadListHelper< Person > badList = new BadListHelper< Person >();

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        synchronized ( badList.list ) {
                            boolean absent = !badList.list.contains( tom );
                            if (absent)
                                badList.list.add( tom );
                        }
                    }
                };
                t.start();

                badList.putIfAbsent( tom );

                // 如果putIfAbsent正确的被同步了，那么无论如何结果size都为1
                // size=2则说明putIfAbsent的同步是错误的（同步锁对象不是list）
                return badList.list.size() == 2;
            }

        } ) );

    }

    @NotThreadSafe
    public class BadListHelper<E> {
        public final List< E > list = Collections.synchronizedList( new ArrayList< E >() );

        public synchronized boolean putIfAbsent(E x) {
            boolean absent = !list.contains( x );

            try {
                TimeUnit.SECONDS.sleep( 5 ); // 为了测试效果
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

            if (absent)
                list.add( x );
            return absent;
        }
    }

    class Person {
        private final String name;

        Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}
