package C4_composing_objects.C4_9_Delegating_thread_safety_to_multiple_underlying_state_variables;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import support.annotations.ThreadSafe;

/**
 * VisualComponent
 * <p/>
 * Delegating thread safety to multiple underlying state variables
 *
 * @author Brian Goetz and Tim Peierls
 * @see <a href=
 *      "http://www.cnblogs.com/dolphin0520/p/3938914.html">Java并发编程：并发容器之CopyOnWriteArrayList</a>
 */
@ThreadSafe
public class VisualComponent {
    private final List< KeyListener > keyListeners = new CopyOnWriteArrayList< KeyListener >();
    private final List< MouseListener > mouseListeners = new CopyOnWriteArrayList< MouseListener >();

    public void addKeyListener(KeyListener listener) {
        keyListeners.add( listener );
    }

    public void addMouseListener(MouseListener listener) {
        mouseListeners.add( listener );
    }

    public void removeKeyListener(KeyListener listener) {
        keyListeners.remove( listener );
    }

    public void removeMouseListener(MouseListener listener) {
        mouseListeners.remove( listener );
    }

    // 之所以它是线程安全的，是因为它同时满足如下条件：
    // 1. keyListeners、mouseListeners是线程安全的
    // 2. keyListeners与mouseListeners没有关系，相互独立。

    // 注意：CopyOnWriteArrayList虽然是线程安全的，但也存在一些问题，它只适合那些读操作远远多于写操作的并发场景
}
