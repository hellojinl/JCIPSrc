package C7_Cancellation_and_Shutdown.Ch7_20_Using_a_private_Executor_whose_lifetime_is_bounded_by_a_method_call;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CheckForMail
 * <p/>
 * Using a private \Executor whose lifetime is bounded by a method call
 *
 * @author Brian Goetz and Tim Peierls
 */
public class CheckForMail {
    public boolean checkMail(Set< String > hosts, long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean( false );
        try {
            for (final String host : hosts)
                exec.execute( new Runnable() {
                    public void run() {
                        if (checkMail( host ))
                            hasNewMail.set( true );
                    }
                } );
        } finally {
            exec.shutdown();
            exec.awaitTermination( timeout, unit );
        }
        return hasNewMail.get();
    }

    private boolean checkMail(String host) {
        // Check for mail
        return false;
    }
}
