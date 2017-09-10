package C5_Building_Blocks.C5_2_Compound_actions_on_Vector_using_client_side_locking;

import java.util.Vector;

/**
 * SafeVectorHelpers
 * <p/>
 * Compound actions on Vector using client-side locking
 *
 * @author Brian Goetz and Tim Peierls
 */
public class SafeVectorHelpers {
    public static Object getLast(Vector list) {
        synchronized ( list ) {
            int lastIndex = list.size() - 1;
            return list.get( lastIndex );
        }
    }

    public static void deleteLast(Vector list) {
        synchronized ( list ) {
            int lastIndex = list.size() - 1;
            list.remove( lastIndex );
        }
    }
}
