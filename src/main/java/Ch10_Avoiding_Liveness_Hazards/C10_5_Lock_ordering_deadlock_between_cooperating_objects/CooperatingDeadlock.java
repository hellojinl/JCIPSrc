package Ch10_Avoiding_Liveness_Hazards.C10_5_Lock_ordering_deadlock_between_cooperating_objects;

import java.util.HashSet;
import java.util.Set;

import support.annotations.Evaluated;
import support.annotations.GuardedBy;

/**
 * CooperatingDeadlock
 * <p/>
 * Lock-ordering deadlock between cooperating objects
 *
 * @author Brian Goetz and Tim Peierls
 */
@Evaluated(">_<")
public class CooperatingDeadlock {
    // Warning: deadlock-prone!
    class Taxi {
        @GuardedBy("this")
        private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return location;
        }

        public synchronized void setLocation(Point location) {
            this.location = location;
            if (location.equals( destination ))
                dispatcher.notifyAvailable( this );
        }

        public synchronized Point getDestination() {
            return destination;
        }

        public synchronized void setDestination(Point destination) {
            this.destination = destination;
        }
    }

    class Dispatcher {
        @GuardedBy("this")
        private final Set< Taxi > taxis;
        @GuardedBy("this")
        private final Set< Taxi > availableTaxis;

        public Dispatcher() {
            taxis = new HashSet< Taxi >();
            availableTaxis = new HashSet< Taxi >();
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add( taxi );
        }

        public synchronized Image getImage() {
            Image image = new Image();
            for (Taxi t : taxis)
                image.drawMarker( t.getLocation() );
            return image;
        }
    }

    class Image {
        public void drawMarker(Point p) {
        }
    }

    interface Point {

    }
}
