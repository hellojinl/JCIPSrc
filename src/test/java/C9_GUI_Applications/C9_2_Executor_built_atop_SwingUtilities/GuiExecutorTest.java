package C9_GUI_Applications.C9_2_Executor_built_atop_SwingUtilities;

import org.junit.Test;

import C9_GUI_Applications.C9_1_Implementing_SwingUtilities_using_an_Executor.SwingUtilities;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class GuiExecutorTest {

    @Test
    public void justRunIt() {
        final GuiExecutor exec = GuiExecutor.instance();
        exec.execute( new PrintRunnable( "1" ) );
        exec.execute( new PrintRunnable( "2" ) );
        exec.execute( new PrintRunnable( "3" ) );
        exec.execute( new PrintRunnable( "4" ) );
        exec.execute( new WhichThreadRunnable() );
    }

    class PrintRunnable implements Runnable {

        private final String msg;

        PrintRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            System.out.println( msg );
        }
    }

    class WhichThreadRunnable implements Runnable {

        @Override
        public void run() {
            boolean r = SwingUtilities.isEventDispatchThread();
            System.out.println( "isEventDispatchThread = " + r );
        }

    }
}
