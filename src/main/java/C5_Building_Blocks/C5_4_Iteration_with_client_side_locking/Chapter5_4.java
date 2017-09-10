package C5_Building_Blocks.C5_4_Iteration_with_client_side_locking;

import java.util.Vector;

public class Chapter5_4 {

    public final Vector< Integer > vector = new Vector< Integer >();

    public void fragment() {
        synchronized ( vector ) {
            for (int i = 0; i < vector.size(); i++) {
                doSomething( vector.get( i ) );
            }
        }
    }

    private void doSomething(Object obj) {

    }
}
