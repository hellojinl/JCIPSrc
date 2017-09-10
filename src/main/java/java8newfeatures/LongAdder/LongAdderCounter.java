package java8newfeatures.LongAdder;

import java.util.concurrent.atomic.LongAdder;

import support.annotations.ThreadSafe;

/**
 * 使用LongAdder实现一个计数器
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@ThreadSafe
public class LongAdderCounter implements ICounter {

    private final LongAdder adder = new LongAdder();

    @Override
    public void increment() {
        adder.increment();
    }

    @Override
    public void decrement() {
        adder.decrement();
    }

    @Override
    public long get() {
        return adder.longValue();
    }

    @Override
    public void reset() {
        adder.reset();
    }

}
