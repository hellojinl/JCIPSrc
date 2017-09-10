package C9_GUI_Applications.C9_8_Initiating_a_long_running_cancellable_task_with_BackgroundTask;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import C9_GUI_Applications.C9_7_Background_task_class_supporting_cancellation_completion_notification_and_progress_notification.BackgroundTask;
import support.RandomUtil;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class RunInBackground {

    private static ExecutorService exec = Executors.newCachedThreadPool();

    private final JButton startButton = new JButton( "1. Start" );
    private final JButton cancelButton = new JButton( "2. Cancel" );
    private final JLabel label = new JLabel( "idle" );
    private final JLabel progress = new JLabel( "progress" );
    private final JFrame frame;

    RunInBackground() {
        frame = new JFrame( "SimpleEventListener" );
        frame.setSize( 300, 300 );// 大小
        frame.setLayout( new BorderLayout() );
        frame.setVisible( true );// 可见
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );// 关闭

        frame.add( startButton, BorderLayout.CENTER );
        frame.add( cancelButton, BorderLayout.NORTH );
        frame.add( progress, BorderLayout.SOUTH );
        frame.add( label, BorderLayout.EAST );
        frame.add( new JPanel(), BorderLayout.WEST );

        runInBackground();
    }

    private void runInBackground() {
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                class CancelListener implements ActionListener {
                    BackgroundTask< ? > task;

                    public void actionPerformed(ActionEvent event) {
                        if (task != null)
                            task.cancel( true );
                    }
                }
                final CancelListener listener = new CancelListener();
                listener.task = new BackgroundTask< Void >() {
                    private final static int MAX = 10;
                    private int i = 0;

                    public Void compute() {
                        label.setText( "busy" );
                        setProgress( i, MAX );
                        while ( moreWork() && !isCancelled() ) {
                            doSomeWork();
                            i++;
                            setProgress( i, MAX );
                        }
                        return null;
                    }

                    private boolean moreWork() {
                        return i < MAX;
                    }

                    private void doSomeWork() {
                        startButton.setText( RandomUtil.get( 0, 1000 ) + "" );
                        try {
                            TimeUnit.SECONDS.sleep( 1 );
                        } catch ( InterruptedException e ) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    protected void onProgress(int current, int max) {
                        progress.setText( current + "/" + max );
                    }

                    protected void onCompletion(Void result, Throwable exception, boolean cancelled) {
                        cancelButton.removeActionListener( listener );
                        label.setText( "done" );
                    }
                };
                cancelButton.addActionListener( listener );
                exec.execute( listener.task );
            }
        } );
    }

    public static void main(String[] args) {
        new RunInBackground();
    }
}
