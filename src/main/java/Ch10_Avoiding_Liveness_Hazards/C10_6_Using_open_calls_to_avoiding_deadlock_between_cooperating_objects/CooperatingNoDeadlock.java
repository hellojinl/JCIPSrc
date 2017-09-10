package Ch10_Avoiding_Liveness_Hazards.C10_6_Using_open_calls_to_avoiding_deadlock_between_cooperating_objects;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;

/**
 * CooperatingNoDeadlock
 * <p/>
 * Using open calls to avoiding deadlock between cooperating objects
 *
 * @author Brian Goetz and Tim Peierls
 */
class CooperatingNoDeadlock {
    @ThreadSafe
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

        public void setLocation(Point location) {
            boolean reachedDestination;
            synchronized ( this ) {
                this.location = location;
                reachedDestination = location.equals( destination );
            }
            if (reachedDestination)
                dispatcher.notifyAvailable( this );
        }

        public synchronized Point getDestination() {
            return destination;
        }

        public synchronized void setDestination(Point destination) {
            this.destination = destination;
        }
    }

    @ThreadSafe
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

        public Image getImage() {
            Set< Taxi > copy;
            synchronized ( this ) {
                copy = new HashSet< Taxi >( taxis );
            }
            Image image = new Image();
            for (Taxi t : copy)
                image.drawMarker( t.getLocation() );
            return image;
        }
    }

    class Image {
        public void drawMarker(Point p) {
        }
    }

}
