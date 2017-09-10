package Ch10_Avoiding_Liveness_Hazards.C10_5_Lock_ordering_deadlock_between_cooperating_objects;

import static org.junit.Assert.fail;

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
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CooperatingDeadlockTest {

    ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * 让Taxi#setLocation和Dispatcher.getImage产生死锁
     */
    @Test
    public void test() {
        final Dispatcher dispatcher = new Dispatcher();
        final Taxi taxi = new Taxi( dispatcher );
        final Point destination = new Point();
        taxi.setDestination( destination );
        dispatcher.addTaxi( taxi );

        exec.submit( () -> {
            // setLocation将获取两个锁，顺序依次为
            // 1.Taxi对象
            // 2.Dispatcher对象
            taxi.setLocation( destination );
        } );

        Future< Image > f = exec.submit( new Callable< Image >() {

            @Override
            public Image call() throws Exception {
                // getImage将获得两个锁，顺序依次为：
                // 1.Dispatcher对象
                // 2.Taxi对象
                return dispatcher.getImage();
            }

        } );

        try {
            f.get( 5, TimeUnit.SECONDS ); // 运行足够的时间
            fail( "应该产生死锁" );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        } catch ( ExecutionException e ) {
            e.printStackTrace();
        } catch ( TimeoutException e ) {
            e.printStackTrace(); // 应该打印出超时异常
        }

    }

    class Taxi {
        @GuardedBy("this")
        private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            System.out.println( "getLocation" );
            return location;
        }

        public synchronized void setLocation(Point location) {
            Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
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

        public synchronized void addTaxi(Taxi taxi) {
            taxis.add( taxi );
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            System.out.println( "notifyAvailable" );
            availableTaxis.add( taxi );
        }

        public synchronized Image getImage() {
            Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
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

    class Point {

    }
}
