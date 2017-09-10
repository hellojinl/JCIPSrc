package Ch11_Performance_and_Scalability.C11_3_Candidate_for_lock_elision;

import org.junit.Test;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ThreeStoogesTest {

    @Test
    public void test() {
        ThreeStooges stooges = new ThreeStooges();
        System.out.println( stooges.getStoogeNames() );

        // Vector是个线程安全的类，但是在getStoogeNames方法内，它显然不需要
        // 同步操作来，JVM能够进行优化？如何确定进行了优化，如何确定没有进行了优化？

    }

}
