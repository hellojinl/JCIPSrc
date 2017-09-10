package C7_Cancellation_and_Shutdown.Ch7_20_Using_a_private_Executor_whose_lifetime_is_bounded_by_a_method_call;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class CheckForMailTest {

    private static final String hasNewMailHost = "1.2.3.4";

    @Test
    public void test() throws InterruptedException {
        Set< String > hosts = new HashSet< String >();
        hosts.add( "127.0.0.1" );
        hosts.add( "127.0.0.2" );
        hosts.add( "127.0.0.3" );
        hosts.add( "127.0.0.4" );
        hosts.add( hasNewMailHost );
        hosts.add( "127.0.0.5" );
        hosts.add( "127.0.0.6" );

        CheckForMail checkForMail = new CheckForMail();
        boolean hasNewMail = checkForMail.checkMail( hosts, 100, TimeUnit.MILLISECONDS );
        assertTrue( hasNewMail );
    }

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
            return hasNewMailHost.equals( host );
        }
    }
}
