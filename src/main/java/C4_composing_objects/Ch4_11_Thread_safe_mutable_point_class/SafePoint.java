package C4_composing_objects.Ch4_11_Thread_safe_mutable_point_class;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * SafePoint
 *
 * @author Brian Goetz and Tim Peierls
 * @see <a href=
 *      "https://stackoverflow.com/questions/12028925/private-constructor-to-avoid-race-condition">private-constructor-to-avoid-race-condition</a>
 */
@ThreadSafe
public class SafePoint {
    @GuardedBy("this")
    private int x, y;

    private SafePoint(int[] a) {
        this( a[0], a[1] );
    }

    public SafePoint(SafePoint p) {
        this( p.get() );
    }

    public SafePoint(int x, int y) {
        this.set( x, y );
    }

    public synchronized int[] get() {
        return new int[] { x, y };
    }

    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
