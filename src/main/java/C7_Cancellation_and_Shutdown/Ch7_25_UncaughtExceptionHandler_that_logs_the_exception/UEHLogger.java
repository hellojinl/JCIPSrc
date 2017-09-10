package C7_Cancellation_and_Shutdown.Ch7_25_UncaughtExceptionHandler_that_logs_the_exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UEHLogger
 * <p/>
 * UncaughtExceptionHandler that logs the exception
 *
 * @author Brian Goetz and Tim Peierls
 */
public class UEHLogger implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log( Level.SEVERE, "Thread terminated with exception: " + t.getName(), e );
    }
}
