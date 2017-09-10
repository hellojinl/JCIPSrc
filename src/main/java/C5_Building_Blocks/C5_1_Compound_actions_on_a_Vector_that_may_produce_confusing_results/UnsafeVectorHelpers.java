package C5_Building_Blocks.C5_1_Compound_actions_on_a_Vector_that_may_produce_confusing_results;

import java.util.Vector;

/**
 * UnsafeVectorHelpers
 * <p/>
 * Compound actions on a Vector that may produce confusing results
 *
 * @author Brian Goetz and Tim Peierls
 */
public class UnsafeVectorHelpers {
    public static Object getLast(Vector list) {
        int lastIndex = list.size() - 1;
        return list.get( lastIndex );
    }

    public static void deleteLast(Vector list) {
        int lastIndex = list.size() - 1;
        list.remove( lastIndex );
    }
}
