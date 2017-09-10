package C2_threadsafety.C2_7_Code_that_would_deadlock_if_intrinsic_locks_were_not_reentrant;

import org.junit.Test;

/**
 * 用于说明子类中通过super调用的synchronized方法，其锁的对象是子类
 * 
 * 代码改编自<url>http://bbs.csdn.net/topics/390448594?page=1</url>第4楼
 * 我在他的基础上增加了2个示例，1.只有一个锁的情况下是什么效果，2.两个锁不相同的时候是什么效果
 * 
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 */
public class SynchronizedTargetTest {

    private static final int SAME_LOCK_TIMES = 5;

    /**
     * 两个方法的锁为同一个
     * 
     * 不管调用几次，'something else'始终无法插入到'1.something sleepy!'与'2.woke
     * up!'之间，因此可以进行如下判断
     * 在TestChild中，doSomethingElse()和super.doSomething()的锁对象是同一个，由于doSomethingElse的所对象肯定是TestChild，
     * 则super.doSomething()的所对象也是TestChild
     * 
     * 它的一次输出 要么是 1.something sleepy! 2.woke up! 3.something else 要么是
     * 3.something else 1.something sleepy! 2.woke up! 肯定不会是 1.something sleepy!
     * 3.something else 2.woke up!
     * 
     * 因为是同一个锁，互斥执行
     */
    @Test
    public void test_when_there_is_the_same_lock() throws InterruptedException {
        System.out.println( "test_when_there_is_the_same_lock>>>>" );
        System.out.println( "-------------------------" );
        for (int i = 0; i < SAME_LOCK_TIMES; i++) {
            final TestChild child = new TestChild();

            Thread anotherTread = new Thread( new Runnable() {
                @Override
                public void run() {
                    child.doSomething();
                }
            } );
            anotherTread.start();

            Thread.sleep( 100 );
            child.doSomethingElse();

            anotherTread.join();
            System.out.println( "-------------------------" );
        }
        System.out.println( "<<<<test_when_there_is_the_same_lock\r\n" );
    }

    private static final int ONLY_ONE_LOCK_TIMES = 5;

    /**
     * 两个方法，只有一个方法有锁
     * 
     * 当doSomethingNoLock没有锁的时候，其输出有可能插入到'1.something sleepy!'与'2.woke up!'之间
     * 
     * 其一次输出有可能出现 1.something sleepy! 3.do something with no lock 2.woke up!
     * 
     * 因为另一个方法没有锁，两个方法将不会互斥执行
     */
    @Test
    public void test_when_there_is_only_one_lock() throws InterruptedException {
        //

        System.out.println( "test_when_there_is_only_one_lock>>>>" );
        System.out.println( "-------------------------" );

        for (int i = 0; i < ONLY_ONE_LOCK_TIMES; i++) {
            final TestChild child = new TestChild();

            Thread anotherTread = new Thread( new Runnable() {
                @Override
                public void run() {
                    child.doSomething();
                }
            } );
            anotherTread.start();

            Thread.sleep( 100 );
            child.doSomethingNoLock();

            anotherTread.join();
            System.out.println( "-------------------------" );
        }

        System.out.println( "<<<<test_when_there_is_only_one_lock\r\n" );
    }

    private static final int DIFFERENT_LOCK_TIMES = 5;

    /**
     * 两个方法它们具有各自不同的锁
     * 
     * 当doSomethingUsingOtherLock使用其他的锁时，其输出将插入到到'1.something sleepy!'与'2.woke
     * up!'之间
     * 
     * 其一次输出有可能是 1.something sleepy! 3.do something with a different lock 2.woke
     * up!
     * 
     * 因为是不同的锁，当然不会互斥执行
     */
    @Test
    public void test_when_there_are_different_locks() throws InterruptedException {

        System.out.println( "test_when_there_are_different_locks>>>>" );
        System.out.println( "-------------------------" );

        for (int i = 0; i < DIFFERENT_LOCK_TIMES; i++) {
            final TestChild child = new TestChild();

            Thread anotherTread = new Thread( new Runnable() {
                @Override
                public void run() {
                    child.doSomething();
                }
            } );
            anotherTread.start();

            Thread.sleep( 100 );
            child.doSomethingUsingOtherLock();

            anotherTread.join();
            System.out.println( "-------------------------" );
        }

        System.out.println( "<<<<test_when_there_are_different_locks\r\n" );
    }

    class TestParent {

        public synchronized void doSomething() {
            System.out.println( "1.something sleepy!" );
            try {
                Thread.sleep( 1000 ); // 休息一秒钟，足够其他线程执行，如果其他线程方法的锁和这里的锁不一致，那么就有可能在这两句输出中插入一句输出。
                System.out.println( "2.woke up!" );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }

    }

    class TestChild extends TestParent {
        public void doSomething() {
            super.doSomething(); // 与doSomethingElse()是同一个锁，锁对象是TestChild
        }

        public synchronized void doSomethingElse() {
            System.out.println( "3.something else" ); // 与super.doSomething()是同一个锁，锁对象是TestChild
        }

        public void doSomethingNoLock() {
            System.out.println( "3.do something with no lock" ); // 没有锁
        }

        private final Object otherLock = new Object();

        public void doSomethingUsingOtherLock() {
            synchronized ( otherLock ) {
                System.out.println( "3.do something with a different lock" ); // 与super.doSomething()不是同一个锁，锁对象是otherLock
            }
        }
    }

}
