package C4_composing_objects.C4_7_Delegating_thread_safety_to_a_ConcurrentHashMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import C4_composing_objects.C4_6_Immutable_Point_class_used_by_DelegatingVehicleTracker.Point;
import support.annotations.ThreadSafe;

/**
 * DelegatingVehicleTracker
 * <p/>
 * Delegating thread safety to a ConcurrentHashMap
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class DelegatingVehicleTracker {
    private final ConcurrentMap< String, Point > locations;
    private final Map< String, Point > unmodifiableMap;

    public DelegatingVehicleTracker(Map< String, Point > points) {
        locations = new ConcurrentHashMap< String, Point >( points );
        unmodifiableMap = Collections.unmodifiableMap( locations );
    }

    public Map< String, Point > getLocations() {
        return unmodifiableMap;
    }

    public Point getLocation(String id) {
        return locations.get( id );
    }

    public void setLocation(String id, int x, int y) {
        if (locations.replace( id, new Point( x, y ) ) == null)
            throw new IllegalArgumentException( "invalid vehicle name: " + id );
    }

    // Alternate version of getLocations (Listing 4.8)
    public Map< String, Point > getLocationsAsStatic() {
        return Collections.unmodifiableMap( new HashMap< String, Point >( locations ) );
    }
}