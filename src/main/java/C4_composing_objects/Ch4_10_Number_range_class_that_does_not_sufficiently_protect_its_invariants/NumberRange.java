package C4_composing_objects.Ch4_10_Number_range_class_that_does_not_sufficiently_protect_its_invariants;

import java.util.concurrent.atomic.AtomicInteger;

import support.annotations.NotThreadSafe;

/**
 * NumberRange
 * <p/>
 * Number range class that does not sufficiently protect its invariants
 *
 * @author Brian Goetz and Tim Peierls
 */
@NotThreadSafe
public class NumberRange {
    // INVARIANT: lower <= upper
    private final AtomicInteger lower = new AtomicInteger( 0 );
    private final AtomicInteger upper = new AtomicInteger( 0 );

    public void setLower(int i) {
        // Warning -- unsafe check-then-act
        if (i > upper.get())
            throw new IllegalArgumentException( "can't set lower to " + i + " > upper" );
        lower.set( i );
    }

    public void setUpper(int i) {
        // Warning -- unsafe check-then-act
        if (i < lower.get())
            throw new IllegalArgumentException( "can't set upper to " + i + " < lower" );
        upper.set( i );
    }

    public boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }

    // 一种可能出错的执行序列
    // [init] range:NumberRange (lower = 50, upper = 100)
    // [thread1 range.setLower(70)] i > upper.get() => 70 > 100 => false
    // [thread2 range.setUpper(60)] i < lower.get() => 60 < 50 => false
    // [thread2 range.setUpper(60)] upper = 60
    // [thread1 range.setLower(70)] lower = 70
    // [error state] lower > upper
    // 主要问题在于（if条件判断完之后并非立即执行接下来的语句，当前线程有可能被挂起，
    // 然后切换到其他线程，当其他线程执行完返回当前线程之后，if条件所做的判断有可能已经失效了，然后再根据失效的判断执行操作就有可能出现错误）
}
