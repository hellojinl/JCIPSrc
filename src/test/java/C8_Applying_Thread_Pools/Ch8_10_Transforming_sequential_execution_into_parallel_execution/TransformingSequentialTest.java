package C8_Applying_Thread_Pools.Ch8_10_Transforming_sequential_execution_into_parallel_execution;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Test;

import C8_Applying_Thread_Pools.Ch8_10_Transforming_sequential_execution_into_parallel_execution.TransformingSequential.Element;
import support.log.MyLogManager;
import testUtils.Timer;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TransformingSequentialTest {

    @Test
    public void test() {
        long timeSeq = time_processSequentially();
        long timePar = time_processInParallel();
        assertTrue( timeSeq > timePar );
    }

    public long time_processSequentially() {
        TransformingSequential t = new TransformingSequentialImpl();
        List< Element > elements = generateElements();
        return Timer.timeMillis( () -> t.processSequentially( elements ) );
    }

    public long time_processInParallel() {
        TransformingSequential t = new TransformingSequentialImpl();
        ExecutorService exec = Executors.newCachedThreadPool();
        List< Element > elements = generateElements();

        return Timer.timeMillis( () -> {
            t.processInParallel( exec, elements );
            exec.shutdown();
            exec.awaitTermination( 5, TimeUnit.SECONDS );
        } );
    }

    private List< Element > generateElements() {
        List< Element > elements = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            elements.add( new ElementImpl( i ) );
        }
        return elements;
    }

    class ElementImpl implements Element {

        private final int no;

        ElementImpl(int no) {
            this.no = no;
        }

        public int getNo() {
            return this.no;
        }

    }

    class TransformingSequentialImpl extends TransformingSequential {

        private final Logger log = MyLogManager.getLogger( "TransformingSequentialImpl" );

        @Override
        public void process(Element e) {
            ElementImpl ei = (ElementImpl) e;
            log.info( "Thread[" + Thread.currentThread().getId() + "] " + ei.getNo() );
        }

    }
}
