package C3_sharing_objects.Ch3_15_Class_at_risk_of_failure_if_not_properly_published;

/**
 * Holder
 * <p/>
 * Class at risk of failure if not properly published
 *
 * @author Brian Goetz and Tim Peierls
 * @see C3_sharing_objects.Ch3_14_Publishing_an_object_without_adequate_synchronization.StuffIntoPublic
 */
public class Holder {
    private int n;

    public Holder(int n) {
        this.n = n;
    }

    public void assertSanity() {
        // 为什么会出现n ！= n的情况？请看如下步骤
        // 0.[其他线程A] 线程A不正确的发布了holder（使用StuffIntoPublic）
        // 1 [当前线程B] 线程B的工作内存中holder的引用被刷新了（可以看见最新的），但是没有刷新n的值（看见的是初始值0）
        // 2.[线程B] 读取n != n 表达左边的值0， 此时表达式为 0 != n
        // 3.[线程B] n的值在工作内存中被刷新了n=42（此时，n的值==主内存中n的值==线程A中n的值）
        // 4.[线程B] 读取 0 != n 右边n的值42，此时表达式为 0 != 42, 结果为true
        if (n != n)
            throw new AssertionError( "This statement is false." );

        // 更进一步，如果n声明为final， 那么即使holder被不正确的发布，也永远不会出现 n != n的情况
        // 因为final对象有一种特殊的初始化安全性保证其在多线程环境下的可见性，
        // 参考文章： http://ifeve.com/jmm-faq-finalright/
    }
}
