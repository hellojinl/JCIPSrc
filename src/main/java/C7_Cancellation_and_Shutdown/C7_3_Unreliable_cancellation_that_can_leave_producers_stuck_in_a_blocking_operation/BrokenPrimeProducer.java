package C7_Cancellation_and_Shutdown.C7_3_Unreliable_cancellation_that_can_leave_producers_stuck_in_a_blocking_operation;

import java.math.BigInteger;
import java.util.concurrent.*;

/**
 * BrokenPrimeProducer
 * <p/>
 * Unreliable cancellation that can leave producers stuck in a blocking
 * operation
 *
 * @author Brian Goetz and Tim Peierls
 */
class BrokenPrimeProducer extends Thread {
    private final BlockingQueue< BigInteger > queue;
    private volatile boolean cancelled = false;

    BrokenPrimeProducer(BlockingQueue< BigInteger > queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while ( !cancelled )
                queue.put( p = p.nextProbablePrime() );
        } catch ( InterruptedException consumed ) {
        }
    }

    public void cancel() {
        cancelled = true;
    }
}
