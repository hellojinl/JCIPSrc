package C7_Cancellation_and_Shutdown.Ch7_26_Registering_a_shutdown_hook_to_stop_the_logging_service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import support.TimeUtil;
import support.log.LogCreator;

public class LogServiceTest {

    /**
     * 当测试运行完毕时，会调用已注册的 shutdownHook
     */
    @Test
    public void justRunIt() throws IOException {
        File file = LogCreator.createLog( "logService.log" );
        LogService logger = new LogService( new FileWriter( file ) );
        logger.start();

        logger.log( "123456" );
        logger.log( "abacdefg" );

        System.out.println( TimeUtil.defaultNow() + " exit..." );
    }
}
