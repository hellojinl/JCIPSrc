package C4_composing_objects.C4_7_Delegating_thread_safety_to_a_ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import C4_composing_objects.C4_6_Immutable_Point_class_used_by_DelegatingVehicleTracker.Point;

public class DelegatingVehicleTrackerTest {

    DelegatingVehicleTracker tracker;

    @Before
    public void setUp() throws Exception {
        Map< String, Point > points = new HashMap< String, Point >();
        points.put( "car1", new Point( 40, 60 ) );
        points.put( "car2", new Point( 200, 120 ) );
        points.put( "car3", new Point( 100, 9 ) );
        tracker = new DelegatingVehicleTracker( points );
    }

    @Test
    public void test_getLocations() {
        Map< String, Point > locations = tracker.getLocations();

        Point point1 = locations.get( "car1" );
        assertEquals( 40, point1.x );
        assertEquals( 60, point1.y );

        Point point2 = locations.get( "car2" );
        assertEquals( 200, point2.x );
        assertEquals( 120, point2.y );

        Point point3 = locations.get( "car3" );
        assertEquals( 100, point3.x );
        assertEquals( 9, point3.y );

        tracker.setLocation( "car1", 1000, 2000 );
        Point newPoint1 = locations.get( "car1" );
        assertEquals( 1000, newPoint1.x ); // getLocations返回的unmodifiableMap会随着DelegatingVehicleTracker.locations的改变而改变
        assertEquals( 2000, newPoint1.y );

        try {
            locations.put( "car1", new Point( 50, 50 ) );
            fail( "unmodifiableMap应该无法修改的" );
        } catch ( UnsupportedOperationException ex ) {

        }

    }

    @Test
    public void test_getLocationsAsStatic() {
        Map< String, Point > locations = tracker.getLocationsAsStatic();

        Point point1 = locations.get( "car1" );
        assertEquals( 40, point1.x );
        assertEquals( 60, point1.y );

        Point point2 = locations.get( "car2" );
        assertEquals( 200, point2.x );
        assertEquals( 120, point2.y );

        Point point3 = locations.get( "car3" );
        assertEquals( 100, point3.x );
        assertEquals( 9, point3.y );

        tracker.setLocation( "car1", 1000, 2000 );
        Point newPoint1 = locations.get( "car1" );
        assertEquals( 40, newPoint1.x ); // getLocationsAsStatic返回的map不会实时的更新数据
        assertEquals( 60, newPoint1.y );

        try {
            locations.put( "car1", new Point( 50, 50 ) );
            fail( "unmodifiableMap应该无法修改的" );
        } catch ( UnsupportedOperationException ex ) {

        }
    }

    // DelegatingVehicleTracker里没有用到synchronized关键字，但它是线程安全的
    // 因为它同时满足如下3点：
    // 1.
    // locations是线程安全的，DelegatingVehicleTracker的获取数据(getLocation)，更新数据(setLocation)都委托给了locations，是线程安全的
    // 2.
    // unmodifiableMap本身是不可修改类，且它内部有一个locations的引用，获取数据的相关操作都是委托给locations，是线程安全的
    // 3.
    // Point是不可变类，是线程安全的，如果用MutablePoint来替换Point,那么DelegatingVehicleTracker将是线程不安全的

}
