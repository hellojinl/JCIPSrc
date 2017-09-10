package C5_Building_Blocks.C5_5_Iterating_a_List_with_an_Iterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chapter5_5 {

    public final List< Widget > widgetList = Collections.synchronizedList( new ArrayList< Widget >() );

    public void fragment() {

        // 可能抛出 ConcurrentModificationException
        for (Widget w : widgetList) {
            doSomething( w );
        }
    }

    private void doSomething(Widget w) {

    }

    class Widget {

    }
}
