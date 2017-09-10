package C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.ThisEscape.Event;
import C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.ThisEscape.EventListener;
import C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.ThisEscape.EventSource;
import C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape.furthermore.ThisEscapeFurtherMoreTest;

public class ThisEscapeTest {

    private final List< EventListener > listenerContainer = new ArrayList< EventListener >();

    /**
     * @see ThisEscapeFurtherMoreTest
     */
    @Test
    public void test_not_recycled() {
        registerListenerByNewThisEscape();
        EventListener listener = listenerContainer.get( 0 );
        // 这里打断点，将看见listener持有对其外部类ThisEscape的引用，进而导致只要listener不被垃圾回收
        // 那么ThisEscape对象也不会被垃圾回收。
        // 此外，假如说ThisEscape的构造函数在registerListener之后还需要进行一些初始化工作，
        // 那么此时ThisEscape处于‘正在构造’的中间状态（不一致的状态）， 如果在其构造完成之前就将它的this引用发布出去了
        // 那么对该this的操作很可能出现错误对象状态不一致的情况，具体见ThisEscapeFurtherMoreTest
        listener.onEvent( new Event() {
        } );
    }

    private void registerListenerByNewThisEscape() {
        EventSource es = new EventSourceImpl();
        // 如下语句创建的对象将不会被垃圾回收，因为它的内部类EventListener将持有对它的引用。
        new ThisEscape( es );
    }

    class EventSourceImpl implements EventSource {

        @Override
        public void registerListener(EventListener e) {
            listenerContainer.add( e );
        }
    }

}
