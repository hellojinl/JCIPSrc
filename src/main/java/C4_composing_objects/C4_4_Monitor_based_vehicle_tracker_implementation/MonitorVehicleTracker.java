package C4_composing_objects.C4_4_Monitor_based_vehicle_tracker_implementation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import C4_composing_objects.C4_5_Mutable_point_class_similar_to_java_awt_Point.MutablePoint;
import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * MonitorVehicleTracker
 * <p/>
 * Monitor-based vehicle tracker implementation
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class MonitorVehicleTracker {
    @GuardedBy("this")
    private final Map< String, MutablePoint > locations;

    public MonitorVehicleTracker(Map< String, MutablePoint > locations) {
        this.locations = deepCopy( locations );
    }

    public synchronized Map< String, MutablePoint > getLocations() {
        return deepCopy( locations );
    }

    public synchronized MutablePoint getLocation(String id) {
        MutablePoint loc = locations.get( id );
        return loc == null ? null : new MutablePoint( loc );
    }

    public synchronized void setLocation(String id, int x, int y) {
        MutablePoint loc = locations.get( id );
        if (loc == null)
            throw new IllegalArgumentException( "No such ID: " + id );
        loc.x = x;
        loc.y = y;
    }

    private static Map< String, MutablePoint > deepCopy(Map< String, MutablePoint > m) {
        Map< String, MutablePoint > result = new HashMap< String, MutablePoint >();

        // for ( String id : m.keySet() )
        // result.put( id, new MutablePoint( m.get( id ) ) );

        // 注释掉的代码是原来的代码（一种不好的写法），所以我在这做了修改
        for (Entry< String, MutablePoint > entry : m.entrySet()) {
            result.put( entry.getKey(), new MutablePoint( entry.getValue() ) );
        }

        return Collections.unmodifiableMap( result );
    }
}
