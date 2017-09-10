package C3_sharing_objects.C3_4_Counting_sheep;

/**
 * CountingSheep
 * <p/>
 * Counting sheep
 *
 * @author Brian Goetz and Tim Peierls
 */
public class CountingSheep {
    volatile boolean asleep;

    void tryToSleep() {
        while ( !asleep )
            countSomeSheep();
    }

    void countSomeSheep() {
        // One, two, three...
    }
}
