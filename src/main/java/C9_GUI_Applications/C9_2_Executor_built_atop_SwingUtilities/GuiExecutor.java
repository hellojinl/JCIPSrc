package C9_GUI_Applications.C9_2_Executor_built_atop_SwingUtilities;

import java.util.*;
import java.util.concurrent.*;

import C9_GUI_Applications.C9_1_Implementing_SwingUtilities_using_an_Executor.SwingUtilities;

/**
 * GuiExecutor
 * <p/>
 * Executor built atop SwingUtilities
 *
 * @author Brian Goetz and Tim Peierls
 */
public class GuiExecutor extends AbstractExecutorService {
    // Singletons have a private constructor and a public factory
    private static final GuiExecutor instance = new GuiExecutor();

    private GuiExecutor() {
    }

    public static GuiExecutor instance() {
        return instance;
    }

    public void execute(Runnable r) {
        if (SwingUtilities.isEventDispatchThread())
            r.run();
        else
            SwingUtilities.invokeLater( r );
    }

    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    public List< Runnable > shutdownNow() {
        throw new UnsupportedOperationException();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }
}
