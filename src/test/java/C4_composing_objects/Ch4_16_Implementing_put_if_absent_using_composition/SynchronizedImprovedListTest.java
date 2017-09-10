package C4_composing_objects.Ch4_16_Implementing_put_if_absent_using_composition;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.sleep.Sleep;
import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class SynchronizedImprovedListTest {

    @Test
    public void test_ThreadSafe() {

        // 返回false表示，企图破坏putIfAbsent原子性的操作在执行了诺干次之后全部失败了
        assertFalse( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                List< Person > list = Collections.synchronizedList( new ArrayList< Person >() );
                final SynchronizedImprovedList< Person > improvedList = new SynchronizedImprovedList< Person >( list );
                final Person tom = new Person( "tom" );

                Thread t = new Thread() {
                    @Override
                    public void run() {

                        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );

                        improvedList.add( 0, tom ); // 企图插入到putIfAbsent去破坏操作原子性，但是无法做到
                    }
                };
                t.start();

                try {
                    improvedList.putIfAbsent( tom );
                    return false;
                } catch ( IllegalStateException illegalState ) {
                    illegalState.printStackTrace();
                    return true; // 永远不会返回true
                }

            }

            public int maximumExecutionTimes() {
                return 3; // 线程安全的操作无论执行几次都不会破坏putIfAbsent操作的原子性，故少执行几次就可以了
            }

        } ) );
    }

    class SynchronizedImprovedList<T> implements List< T > {
        private final List< T > list;

        /**
         * PRE: list argument is thread-safe.
         */
        public SynchronizedImprovedList(List< T > list) {
            this.list = list;
        }

        public synchronized boolean putIfAbsent(T x) {
            boolean contains = list.contains( x );

            // 注意： 如果putIfAbsent具有原子性，那么增加了如下代码它也仍然具有原子性
            // 为了测试效果，补充代码>>>>

            Sleep.sleepUninterruptibly( 5, TimeUnit.SECONDS );

            boolean contains2 = list.contains( x );
            if (contains != contains2) {
                // 前后两次值不相等则说明原子性被破坏了，有其他操作修改了list
                throw new IllegalStateException();
            }

            // <<<<为测试效果，补充的代码

            if (!contains)
                list.add( x );
            return !contains;
        }

        public synchronized int size() {
            return list.size();
        }

        public synchronized boolean isEmpty() {
            return list.isEmpty();
        }

        public synchronized boolean contains(Object o) {
            return list.contains( o );
        }

        public synchronized Object[] toArray() {
            return list.toArray();
        }

        public synchronized <T> T[] toArray(T[] a) {
            return list.toArray( a );
        }

        public synchronized boolean add(T e) {
            return list.add( e );
        }

        public synchronized boolean remove(Object o) {
            return list.remove( o );
        }

        public synchronized boolean containsAll(Collection< ? > c) {
            return list.containsAll( c );
        }

        public synchronized boolean addAll(Collection< ? extends T > c) {
            return list.addAll( c );
        }

        public synchronized boolean addAll(int index, Collection< ? extends T > c) {
            return list.addAll( index, c );
        }

        public synchronized boolean removeAll(Collection< ? > c) {
            return list.removeAll( c );
        }

        public synchronized boolean retainAll(Collection< ? > c) {
            return list.retainAll( c );
        }

        public synchronized boolean equals(Object o) {
            return list.equals( o );
        }

        public synchronized int hashCode() {
            return list.hashCode();
        }

        public synchronized T get(int index) {
            return list.get( index );
        }

        public synchronized T set(int index, T element) {
            return list.set( index, element );
        }

        public synchronized void add(int index, T element) {
            list.add( index, element );
        }

        public synchronized T remove(int index) {
            return list.remove( index );
        }

        public synchronized int indexOf(Object o) {
            return list.indexOf( o );
        }

        public synchronized int lastIndexOf(Object o) {
            return list.lastIndexOf( o );
        }

        public synchronized List< T > subList(int fromIndex, int toIndex) {
            return list.subList( fromIndex, toIndex );
        }

        public synchronized void clear() {
            list.clear();
        }

        public Iterator< T > iterator() {
            return list.iterator(); // Must be manually synched by user!
        }

        public ListIterator< T > listIterator() {
            return list.listIterator(); // Must be manually synched by user
        }

        public ListIterator< T > listIterator(int index) {
            return list.listIterator( index ); // Must be manually synched by
                                               // user
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
