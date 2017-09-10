package C8_Applying_Thread_Pools.C8_6_Custom_thread_factory;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.Test;

import support.log.MyLogManager;

/**
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * @see <a href="http://blog.sina.com.cn/s/blog_70174db20100ll8d.html">java
 *      log中的FINE，FINER和FINEST</a>
 */
public class LogTipTest {

    private static final Logger log = MyLogManager.getLogger( "testLog" );

    /**
     * 可以通过同时修改.level和java.util.logging.ConsoleHandler.level两个属性观察控制台输出结果
     */
    @Test
    public void test() throws FileNotFoundException {

        log.finest( "a finest message" );
        log.finer( "a finer message" );
        log.fine( "a fine message" );
        log.config( "a config message" );

        log.info( "a info message" );
        log.warning( "a warning message" );
        log.severe( "a severe message" );
    }
}
