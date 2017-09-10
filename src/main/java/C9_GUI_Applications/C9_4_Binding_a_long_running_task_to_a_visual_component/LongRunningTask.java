package C9_GUI_Applications.C9_4_Binding_a_long_running_task_to_a_visual_component;

import java.awt.BorderLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import support.RandomUtil;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LongRunningTask {

    private static ExecutorService exec = Executors.newCachedThreadPool();
    private final JButton computeButton = new JButton( "Big computation" );
    private final JFrame frame;

    LongRunningTask() {
        frame = new JFrame( "SimpleEventListener" );
        frame.setSize( 300, 300 );// 大小
        frame.setLayout( new BorderLayout() );
        frame.setVisible( true );// 可见
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );// 关闭

        frame.add( computeButton, BorderLayout.CENTER );
        frame.add( new JPanel(), BorderLayout.NORTH );
        frame.add( new JPanel(), BorderLayout.SOUTH );
        frame.add( new JPanel(), BorderLayout.EAST );
        frame.add( new JPanel(), BorderLayout.WEST );

        longRunningTask();
    }

    private void longRunningTask() {
        computeButton.addActionListener( event -> {
            exec.execute( () -> {
                /* Do big computation */
                computeButton.setText( "do big computation..." );
                try {
                    TimeUnit.SECONDS.sleep( 1 );
                } catch ( InterruptedException ignored ) {

                }
                computeButton.setText( RandomUtil.get( 0, 10000 ) + "" );
            } );
        } );
    }

    public static void main(String[] args) {
        new LongRunningTask();
    }
}
