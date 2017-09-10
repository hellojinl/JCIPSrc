package C4_composing_objects.Ch4_15_Implementing_put_if_absent_with_client_side_locking;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.annotations.ThreadSafe;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class GoodListHelperTest {

    @Test
    public void test() {
        assertFalse( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                final Person tom = new Person( "tom" );
                final GoodListHelper< Person > goodList = new GoodListHelper< Person >();

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        synchronized ( goodList.list ) {
                            boolean absent = !goodList.list.contains( tom );
                            if (absent)
                                goodList.list.add( tom );
                        }
                    }
                };
                t.start();

                goodList.putIfAbsent( tom );

                // 如果putIfAbsent正确的被同步了，那么无论如何结果size都为1
                // size=2则说明putIfAbsent的同步是错误的
                return goodList.list.size() == 2;
            }

            @Override
            public int maximumExecutionTimes() {
                return 5; // 无论执行多少次size都不可能等于2，所以少执行几次
            }

        } ) );

    }

    @ThreadSafe
    class GoodListHelper<E> {
        public List< E > list = Collections.synchronizedList( new ArrayList< E >() );

        public boolean putIfAbsent(E x) {
            synchronized ( list ) {
                boolean absent = !list.contains( x );

                try {
                    TimeUnit.SECONDS.sleep( 1 ); // 为了测试效果，事实上不考虑效率的话，这里无论休眠多久，结果都是对的
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }

                if (absent)
                    list.add( x );
                return absent;
            }
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
