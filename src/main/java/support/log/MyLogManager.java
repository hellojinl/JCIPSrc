package support.log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * JDK Logger读取指定配置文件
 * <p>
 * jdk
 * logger的默认配置文件在jdk的jre的lib下的logging.properties文件（例如：在我得电脑上是D:\Java\jdk1.8.0_11\jre\lib\logging.properties）
 * 可以通过同时修改.level和java.util.logging.ConsoleHandler.level两个属性的日志级别来改变显示在控制台上的日志。
 * <p>
 * 为了方便起见，这里采用另一种方式，即使用指定的配置文件（同目录下的logging.properties）。
 * <p>
 * <b>注意：只保证使用这里的getLogger和getAnonymousLogger能采用正确的属性配置，如果在其他类里单独使用Logger.getLogger则不一定能读取指定的配置文件（取决于MyLogManager是否已加载）</b>
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 * @see <a href=
 *      "http://blog.csdn.net/sunzhenhua0608/article/details/10046349">Java API
 *      自带的日志管理，可配置文件路径，并自动创建不存在的目录</a>
 * @see C8_Applying_Thread_Pools.C8_6_Custom_thread_factory.LogTipTest 使用示例
 */
public final class MyLogManager {

    private final static String LOGGING_PROPERTIES_PATH = MyLogManager.class.getResource( "" ).getPath()
            + "/logging.properties";

    static {
        try ( InputStream inputStream = new FileInputStream( LOGGING_PROPERTIES_PATH ); ) {
            LogManager logManager = LogManager.getLogManager();
            logManager.readConfiguration( inputStream );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private MyLogManager() {

    }

    public static Logger getLogger(String name) {
        return Logger.getLogger( name );
    }

    public static Logger getAnonymousLogger() {
        return Logger.getAnonymousLogger();
    }
}
