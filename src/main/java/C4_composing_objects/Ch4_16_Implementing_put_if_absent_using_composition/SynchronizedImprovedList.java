package C4_composing_objects.Ch4_16_Implementing_put_if_absent_using_composition;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 同步的ImprovedList
 * 大部分方法是线程安全的（除了遍历方法，因为synchronized无法确保遍历操作的线程安全性，线程安全对象的复合操作（遍历操作是一种复合操作）不一定是线程安全的（见第5章））
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SynchronizedImprovedList<T> implements List< T > {
    private final List< T > list;

    /**
     * PRE: list argument is thread-safe.
     */
    public SynchronizedImprovedList(List< T > list) {
        this.list = list;
    }

    public synchronized boolean putIfAbsent(T x) {
        boolean contains = list.contains( x );
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
        return list.listIterator( index ); // Must be manually synched by user
    }
}
