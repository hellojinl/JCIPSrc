package C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.furthermore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.furthermore.ThisEscape.EventListener;
import C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.furthermore.ThisEscape.EventSource;
import testUtils.ConcurrentTestExecutor;

/**
 * 
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 *
 */
public class ThisEscapeFurtherMoreTest {

    private final ConcurrentLinkedQueue< EventListener > listeners = new ConcurrentLinkedQueue< EventListener >();

    @Before
    public void setUp() throws Exception {
        listeners.clear();
    }

    /**
     * 正常执行
     * <p>
     * 如果ThisEscape在构造函数执行完成后被发布，那么它的numValue将为1;
     */
    @Test
    public void test_in_single_thread() {
        EventSource eventSource = new EventSourceImpl();
        ThisEscape thisEscape = new ThisEscape( eventSource );
        assertEquals( 1, thisEscape.getNumValue() );
    }

    /**
     * 试图利用提前发布的this进行破坏
     * <p>
     * ThisEscape对象的引用在构造函数完成前被发布，在另一个线程中利用这个提前发布的引用
     * 将num设置为null，意与破坏其构造函数，如果该操作在构造函数中的num.getAndIncrement()之前执行，
     * 那么构造函数将抛出NullPointerException。
     * 这就说明在构造函数完成之前，发布this引用，将会使得ThisEscape的构造行为变得不可预测（有时候对，有时候错）。
     * 
     */
    @Test
    public void test_in_two_thread() throws InterruptedException, ExecutionException {
        assertTrue( ConcurrentTestExecutor.repeatedExecute( this::tryOnce ) );
    }

    private boolean tryOnce() throws InterruptedException, ExecutionException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future< Integer > future = executor.submit( new Callable< Integer >() {

                @Override
                public Integer call() throws Exception {
                    EventSource eventSource = new EventSourceImpl();
                    ThisEscape thisEscape = new ThisEscape( eventSource );
                    return thisEscape.getNumValue();
                }

            } );

            EventListener eventListener = listeners.peek();
            ThisEscape thisEscape = eventListener.getThisEscape(); // 这个引用很有可能是构造函数（ThisEscape）还没完成时发布的！
            thisEscape.setNum( null );

            Integer result = future.get();
            if (result == 1) {
                return false; // EventListener构造函数正常结束，破坏没成功
            } else {
                return true; // 实际上，不可能出现这种情况
            }
        } catch ( NullPointerException nullPointer ) {
            return true; // 符合预期，从另一个线程破坏了构造函数的执行
        }
    }

    class EventSourceImpl implements EventSource {

        @Override
        public void registerListener(EventListener e) {
            listeners.add( e );
        }
    }
}
