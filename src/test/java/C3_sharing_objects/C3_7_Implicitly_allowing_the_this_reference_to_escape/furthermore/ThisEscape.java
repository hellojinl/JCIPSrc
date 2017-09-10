package C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.furthermore;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThisEscape
 * <p/>
 * Implicitly allowing the this reference to escape
 *
 * @author Brian Goetz and Tim Peierls
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ThisEscape {

    private AtomicInteger num = new AtomicInteger();

    public ThisEscape(EventSource source) {

        /**
         * 还没构造完，就把this发布了，我们会用测试破坏它的构造过程
         */
        source.registerListener( new EventListener() {
            public void onEvent(Event e) {
                doSomething( e );
            }

            public ThisEscape getThisEscape() {
                return ThisEscape.this;
            }
        } );

        try {
            TimeUnit.SECONDS.sleep( 5 ); // 为了让测试效果明显
        } catch ( InterruptedException interrupted ) {
            throw new RuntimeException( interrupted );
        }

        num.getAndIncrement();
    }

    public void setNum(AtomicInteger newNum) {
        this.num = newNum;
    }

    public void setNumValue(int newValue) {
        num.set( newValue );
    }

    public int getNumValue() {
        return num.get();
    }

    void doSomething(Event e) {
    }

    interface EventSource {
        void registerListener(EventListener e);
    }

    interface EventListener {
        void onEvent(Event e);

        ThisEscape getThisEscape();
    }

    interface Event {
    }
}
