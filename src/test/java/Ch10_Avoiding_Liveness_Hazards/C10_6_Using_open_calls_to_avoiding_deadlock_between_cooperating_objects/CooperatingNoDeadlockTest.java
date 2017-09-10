package Ch10_Avoiding_Liveness_Hazards.C10_6_Using_open_calls_to_avoiding_deadlock_between_cooperating_objects;

import static org.junit.Assert.assertNotNull;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import support.annotations.GuardedBy;
import support.annotations.ThreadSafe;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CooperatingNoDeadlockTest {

    ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * 不存在死锁的情况
     */
    @Test
    public void test() {
        final Dispatcher dispatcher = new Dispatcher();
        final Taxi taxi = new Taxi( dispatcher );
        final Point destination = new Point();
        taxi.setDestination( destination );
        dispatcher.addTaxi( taxi );

        exec.submit( () -> {
            taxi.setLocation( destination );
        } );

        Future< Image > f = exec.submit( new Callable< Image >() {

            @Override
            public Image call() throws Exception {
                return dispatcher.getImage();
            }

        } );

        try {
            Image img = f.get( 5, TimeUnit.SECONDS ); // 运行足够的时间
            assertNotNull( img );
        } catch ( InterruptedException | ExecutionException | TimeoutException e ) {
            e.printStackTrace();
        }
    }

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
            Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
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

        public synchronized void addTaxi(Taxi taxi) {
            taxis.add( taxi );
        }

        public Image getImage() {
            Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
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
