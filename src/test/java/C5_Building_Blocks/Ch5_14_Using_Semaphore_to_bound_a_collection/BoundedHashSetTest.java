package C5_Building_Blocks.Ch5_14_Using_Semaphore_to_bound_a_collection;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.sleep.Sleep;

public class BoundedHashSetTest {

    @Test
    public void test() throws InterruptedException {
        final BoundedHashSet< Integer > set = new BoundedHashSet< Integer >( 1 );
        set.add( 1 );
        Thread t = new Thread( new Runnable() {

            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS );
                    System.out.println( "wait " + i + "s" );
                }
                set.remove( 1 );
            }
        } );
        t.start();

        set.add( 2 );
        System.out.println( "It has just been completed that the set was added 2." );
    }
}
