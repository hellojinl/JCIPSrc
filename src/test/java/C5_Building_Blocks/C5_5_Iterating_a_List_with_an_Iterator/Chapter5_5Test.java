package C5_Building_Blocks.C5_5_Iterating_a_List_with_an_Iterator;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import testUtils.ConcurrentTestExecutor;
import testUtils.ConcurrentTestExecutor.ConcurrentTestCallback;

public class Chapter5_5Test {

    final List< Widget > widgetList = Collections.synchronizedList( new ArrayList< Widget >() );

    @Before
    public void setUp() throws Exception {
        widgetList.add( new Widget( "1st" ) );
        widgetList.add( new Widget( "2nd" ) );
        widgetList.add( new Widget( "3rd" ) );
    }

    @Test
    public void test() {

        assertTrue( ConcurrentTestExecutor.repeatedExecute( new ConcurrentTestCallback() {

            @Override
            public boolean doConcurrentTest() throws Exception {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            widgetList.add( new Widget( i + "" ) );
                        }
                    }
                };
                t.start();

                try {
                    for (Widget w : widgetList) {
                        doSomething( w );
                    }
                    return false;
                } catch ( ConcurrentModificationException ex ) {
                    return true; // 抛出 ConcurrentModificationException是期待的结果
                }
            }

        } ) );

    }

    private void doSomething(Widget w) {
        System.out.println( w );
    }

    class Widget {
        private final String name;

        Widget(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

}
