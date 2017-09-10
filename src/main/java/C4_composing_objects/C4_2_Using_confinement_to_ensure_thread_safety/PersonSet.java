package C4_composing_objects.C4_2_Using_confinement_to_ensure_thread_safety;

import java.util.HashSet;
import java.util.Set;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * PersonSet
 * <p/>
 * Using confinement to ensure thread safety
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class PersonSet {
    @GuardedBy("this")
    private final Set< Person > mySet = new HashSet< Person >();

    public synchronized void addPerson(Person p) {
        mySet.add( p );
    }

    public synchronized boolean containsPerson(Person p) {
        return mySet.contains( p );
    }

    interface Person {
    }

    // 为什么PersonSet是线程安全性的？
    // 首先，HashSet是线程不安全的，但是它被封闭在PersonSet内部（外界不可访问且没有发布出去）
    // 其次，对HashSet的所有操作都使用了同步。
    // 最后，Person是否线程安全对PersonSet是否线程安全没有任何影响，
    // 因为HashSet里只保存了Person的引用，不论Person的状态如何变化，其引用始终是不变的（HashSet里保存的值不会随着Person状态的改变而改变）
    // 但是，话又说回来，从全局来看，如果要保证系统的线程安全，则Person对象的线程安全性也必须要得到保证，那就需要额外的同步了
    // 即 客户端在使用Person对象的每一个地方都要同步 或者 Person对象是一个不可变对象

}
