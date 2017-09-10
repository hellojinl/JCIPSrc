package java8newfeatures.LongAdder;

import java.util.concurrent.atomic.AtomicLong;

import support.annotations.ThreadSafe;

/**
 * 使用AtomicLong实现一个计数器
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
@ThreadSafe
public class AtomicLongCounter implements ICounter {

    private final AtomicLong adder = new AtomicLong();

    @Override
    public void increment() {
        adder.incrementAndGet();
    }

    @Override
    public void decrement() {
        adder.decrementAndGet();
    }

    @Override
    public long get() {
        return adder.get();
    }

    @Override
    public void reset() {
        adder.set( 0L );
    }

}
