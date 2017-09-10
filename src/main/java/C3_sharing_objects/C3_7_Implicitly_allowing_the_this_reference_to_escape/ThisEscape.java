package C3_sharing_objects.C3_7_Implicitly_allowing_the_this_reference_to_escape;

/**
 * ThisEscape
 * <p/>
 * Implicitly allowing the this reference to escape
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThisEscape {
    public ThisEscape(EventSource source) {
        source.registerListener( new EventListener() {
            public void onEvent(Event e) {
                doSomething( e );
            }
        } );
    }

    void doSomething(Event e) {
    }

    interface EventSource {
        void registerListener(EventListener e);
    }

    interface EventListener {
        void onEvent(Event e);
    }

    interface Event {
    }
}
