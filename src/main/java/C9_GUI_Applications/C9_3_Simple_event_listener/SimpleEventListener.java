package C9_GUI_Applications.C9_3_Simple_event_listener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SimpleEventListener {

    public static void main(String[] args) {
        JFrame frame = new JFrame( "SimpleEventListener" );
        frame.setSize( 300, 300 );// 大小
        frame.setLayout( new BorderLayout() );
        frame.setVisible( true );// 可见
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );// 关闭

        Random random = new Random();
        JButton bigButton = new JButton( "Change Color" );

        AtomicInteger color = new AtomicInteger();

        bigButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                color.set( random.nextInt() );
                bigButton.setBackground( new Color( color.get() ) );
            }

        } );

        // 另一种写法
        bigButton.addActionListener( e -> {
            bigButton.setText( " color = " + color.get() + "" );
        } );

        frame.add( bigButton, BorderLayout.CENTER );
        frame.add( new JPanel(), BorderLayout.NORTH );
        frame.add( new JPanel(), BorderLayout.SOUTH );
        frame.add( new JPanel(), BorderLayout.EAST );
        frame.add( new JPanel(), BorderLayout.WEST );
    }
}
