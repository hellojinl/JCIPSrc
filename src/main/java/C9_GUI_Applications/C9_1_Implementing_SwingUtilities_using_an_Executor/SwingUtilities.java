package C9_GUI_Applications.C9_1_Implementing_SwingUtilities_using_an_Executor;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * SwingUtilities
 * <p/>
 * Implementing SwingUtilities using an Executor
 *
 * @author Brian Goetz and Tim Peierls
 */
public class SwingUtilities {
    private static final ExecutorService exec = Executors.newSingleThreadExecutor( new SwingThreadFactory() );
    private static volatile Thread swingThread;

    private static class SwingThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            swingThread = new Thread( r );
            return swingThread;
        }
    }

    public static boolean isEventDispatchThread() {
        return Thread.currentThread() == swingThread;
    }

    public static void invokeLater(Runnable task) {
        exec.execute( task );
    }

    public static void invokeAndWait(Runnable task) throws InterruptedException, InvocationTargetException {
        Future f = exec.submit( task );
        try {
            f.get();
        } catch ( ExecutionException e ) {
            throw new InvocationTargetException( e );
        }
    }
}
