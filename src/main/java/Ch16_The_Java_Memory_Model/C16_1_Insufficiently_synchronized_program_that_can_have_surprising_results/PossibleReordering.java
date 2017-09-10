package Ch16_The_Java_Memory_Model.C16_1_Insufficiently_synchronized_program_that_can_have_surprising_results;

import support.annotations.Evaluated;

/**
 * PossibleReordering
 * <p/>
 * Insufficiently synchronized program that can have surprising results
 *
 * @author Brian Goetz and Tim Peierls
 */
@Evaluated(">_<")
public class PossibleReordering {
    static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread one = new Thread( new Runnable() {
            public void run() {
                a = 1;
                x = b;
            }
        } );
        Thread other = new Thread( new Runnable() {
            public void run() {
                b = 1;
                y = a;
            }
        } );
        one.start();
        other.start();
        one.join();
        other.join();
        System.out.println( "( " + x + "," + y + ")" );
    }
}
