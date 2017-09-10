package C3_sharing_objects.C3_3_Thread_safe_mutable_integer_holder;

import org.junit.Test;

/**
 * SynchronizedInteger的线程安全性是由Synchronized关键字保证的
 * <ul>
 * Synchronized特性：
 * <li>原子性（同步特性）</li>
 * <li>内存可见性</li>
 * </ul>
 * 
 * <ul>
 * JMM关于Synchronized的两条规定：
 * <li>线程解锁前，必须把共享变量的最新值刷新到主内存中；</li>
 * <li>线程加锁时，讲清空工作内存中共享变量的值，从而使用共享变量是需要从主内存中重新读取最新的值（加锁与解锁需要统一把锁）</li>
 * </ul>
 * 
 * <ul>
 * 线程执行互斥锁代码的过程：
 * <li>1.获得互斥锁</li>
 * <li>2.清空工作内存</li>
 * <li>3.从主内存拷贝最新变量副本到工作内存</li>
 * <li>4.执行代码块</li>
 * <li>5.将更改后的共享变量的值刷新到主内存中</li>
 * <li>6.释放互斥锁</li>
 * </ul>
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see <a href=
 *      "http://blog.csdn.net/lymmm2/article/details/46627097">Synchronized实现</a>
 */
public class SynchronizedIntegerTest {

    @Test
    public void test() {

    }

}
