package C9_GUI_Applications.C9_6_Cancelling_a_long_running_task;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class TaskWithCancellation {

    private static ExecutorService exec = Executors.newCachedThreadPool();

    private final JButton startButton = new JButton( "1. Start" );
    private final JButton cancelButton = new JButton( "2. Cancel" );
    private Future< ? > runningTask = null; // thread-confined
    private final JLabel label = new JLabel( "idle" );
    private final JFrame frame;

    TaskWithCancellation() {
        frame = new JFrame( "SimpleEventListener" );
        frame.setSize( 300, 300 );// 大小
        frame.setLayout( new BorderLayout() );
        frame.setVisible( true );// 可见
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );// 关闭

        frame.add( startButton, BorderLayout.CENTER );
        frame.add( cancelButton, BorderLayout.NORTH );
        frame.add( label, BorderLayout.SOUTH );
        frame.add( new JPanel(), BorderLayout.EAST );
        frame.add( new JPanel(), BorderLayout.WEST );

        taskWithCancellation();
    }

    private void taskWithCancellation() {
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (runningTask == null) {
                    runningTask = exec.submit( new Runnable() {
                        public void run() {
                            while ( moreWork() ) {
                                if (Thread.currentThread().isInterrupted()) {
                                    cleanUpPartialWork();
                                    break;
                                }
                                doSomeWork();
                            }
                        }

                        private boolean moreWork() {
                            return true;
                        }

                        private void cleanUpPartialWork() {
                            label.setText( "cancelled" );
                            runningTask = null;
                        }

                        private void doSomeWork() {
                            label.setText( "doing..." );
                            try {
                                TimeUnit.SECONDS.sleep( 1 );
                            } catch ( InterruptedException e ) {
                                Thread.currentThread().interrupt();
                            }
                        }

                    } );
                }
                ;
            }
        } );

        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (runningTask != null)
                    runningTask.cancel( true );
            }
        } );
    }

    public static void main(String[] args) {
        new TaskWithCancellation();
    }
}
