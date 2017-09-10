package C9_GUI_Applications.C9_1_Implementing_SwingUtilities_using_an_Executor;

import static org.junit.Assert.assertFalse;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SwingUtilitiesTest {

    @Test
    public void justRunIt() throws InvocationTargetException, InterruptedException {
        assertFalse( SwingUtilities.isEventDispatchThread() );

        SwingUtilities.invokeLater( new PrintRunnable( "1" ) );
        SwingUtilities.invokeLater( new PrintRunnable( "2" ) );
        SwingUtilities.invokeLater( new PrintRunnable( "3" ) );
        SwingUtilities.invokeLater( new PrintRunnable( "4" ) );

        SwingUtilities.invokeAndWait( new WhichThreadRunnable() );
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
