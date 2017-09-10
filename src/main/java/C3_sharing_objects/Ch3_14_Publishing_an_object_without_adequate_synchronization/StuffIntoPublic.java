package C3_sharing_objects.Ch3_14_Publishing_an_object_without_adequate_synchronization;

import C3_sharing_objects.Ch3_15_Class_at_risk_of_failure_if_not_properly_published.Holder;

/**
 * StuffIntoPublic
 * <p/>
 * Unsafe publication
 *
 * @author Brian Goetz and Tim Peierls
 * @see <a href=
 *      "http://www.cnblogs.com/java-zhao/p/5124725.html">Java内存模型与共享变量可见性</a>
 */
public class StuffIntoPublic {
    public Holder holder;

    public void initialize() {
        holder = new Holder( 42 );
    }

    // 之所以说这么发布是不正确的，是因为在多线程环境下holder变量没有任何可见性的保证（volatile或同步），
    // 那么当多个线程交替执行的时候有可能出现问题，例如A线程已经发布了holder的新值，但是B线程在之后的一段时间内看到的仍然是holder的旧值
    // 还有另一种更糟糕的情况，holder引用与其指向对象的属性都属于共享变量，那么由于主内存和线程本地工作内存刷新的问题，有可能出现
    // holder引用刷新了（可见）而具体的属性未刷新（已经在其他线程赋值，但是对于未刷新工作内存的线程是不可见的）这种状态
    // 参考：《内存模型和共享变量可见性》
}
