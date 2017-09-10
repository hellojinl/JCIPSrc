package Ch16_The_Java_Memory_Model.C16_8_Initialization_safety_for_immutable_objects;

import java.util.HashMap;
import java.util.Map;

import support.annotations.ThreadSafe;

/**
 * SafeStates
 * <p/>
 * Initialization safety for immutable objects
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class SafeStates {
    private final Map< String, String > states;

    public SafeStates() {
        states = new HashMap< String, String >();
        states.put( "alaska", "AK" );
        states.put( "alabama", "AL" );
        /* ... */
        states.put( "wyoming", "WY" );
    }

    public String getAbbreviation(String s) {
        return states.get( s );
    }
}
