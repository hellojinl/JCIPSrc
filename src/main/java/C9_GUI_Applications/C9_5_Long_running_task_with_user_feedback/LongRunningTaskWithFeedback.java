package C9_GUI_Applications.C9_5_Long_running_task_with_user_feedback;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import C9_GUI_Applications.C9_2_Executor_built_atop_SwingUtilities.GuiExecutor;
import support.RandomUtil;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LongRunningTaskWithFeedback {

    private static ExecutorService exec = Executors.newCachedThreadPool();

    private final JButton button = new JButton( "Do" );
    private final JLabel label = new JLabel( "idle" );
    private final JLabel resultLabel = new JLabel( "00000" );
    private final JFrame frame;

    LongRunningTaskWithFeedback() {
        frame = new JFrame( "SimpleEventListener" );
        frame.setSize( 300, 300 );// 大小
        frame.setLayout( new BorderLayout() );
        frame.setVisible( true );// 可见
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );// 关闭

        frame.add( button, BorderLayout.CENTER );
        frame.add( label, BorderLayout.WEST );
        frame.add( resultLabel, BorderLayout.EAST );
        frame.add( new JPanel(), BorderLayout.NORTH );
        frame.add( new JPanel(), BorderLayout.SOUTH );
        longRunningTaskWithFeedback();
    }

    private void longRunningTaskWithFeedback() {
        button.addActionListener( event -> {
            button.setEnabled( false );
            label.setText( "busy" );
            exec.execute( () -> {
                try {
                    try {
                        TimeUnit.SECONDS.sleep( 1 );
                    } catch ( InterruptedException ignored ) {

                    }
                    resultLabel.setText( RandomUtil.get( 0, 10000 ) + "" );
                } finally {
                    GuiExecutor.instance().execute( new Runnable() {
                        public void run() {
                            button.setEnabled( true );
                            label.setText( "idle" );
                        }
                    } );
                }
            } );
        } );
    }

    public static void main(String[] args) {
        new LongRunningTaskWithFeedback();
    }
}
