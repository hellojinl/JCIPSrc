package Ch11_Performance_and_Scalability.C11_4_Holding_a_lock_longer_than_necessary;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import support.annotations.Evaluated;
import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * AttributeStore
 * <p/>
 * Holding a lock longer than necessary
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class AttributeStore {
    @GuardedBy("this")
    private final Map< String, String > attributes = new HashMap< String, String >();

    @Evaluated("-_-")
    public synchronized boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get( key );
        if (location == null)
            return false;
        else
            return Pattern.matches( regexp, location );
    }
}
