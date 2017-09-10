package C3_sharing_objects.C3_1_Sharing_variables_without_synchronization;

/**
 * NoVisibility
 * <p/>
 * Sharing variables without synchronization
 *
 * @author Brian Goetz and Tim Peierls
 */

public class NoVisibility {
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread {
        public void run() {
            while ( !ready )
                Thread.yield();
            System.out.println( number );
        }
    }

    public static void main(String[] args) {
        new ReaderThread().start();
        number = 42;
        ready = true;
    }
}
