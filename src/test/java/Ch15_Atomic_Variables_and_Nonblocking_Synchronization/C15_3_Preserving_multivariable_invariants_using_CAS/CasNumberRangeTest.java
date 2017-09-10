package Ch15_Atomic_Variables_and_Nonblocking_Synchronization.C15_3_Preserving_multivariable_invariants_using_CAS;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.RandomUtil;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class CasNumberRangeTest {

    ExecutorService pool = Executors.newCachedThreadPool();

    CasNumberRange range = new CasNumberRange();

    @Test
    public void test() throws InterruptedException {
        pool.execute( new LowerRunnable() );
        pool.execute( new UpperRunnable() );

        pool.shutdown();
        pool.awaitTermination( 2, TimeUnit.SECONDS );

        System.out.println( String.format( "[%d, %d]", range.getLower(), range.getUpper() ) );
        assertTrue( range.getLower() <= range.getUpper() );
    }

    class LowerRunnable implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                try {
                    range.setLower( RandomUtil.get( 0, 100000 ) );
                } catch ( RuntimeException ignored ) {

                }

            }
        }

    }

    class UpperRunnable implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                try {
                    range.setUpper( RandomUtil.get( 0, 100000 ) );
                } catch ( RuntimeException ignored ) {

                }

            }
        }

    }
}
