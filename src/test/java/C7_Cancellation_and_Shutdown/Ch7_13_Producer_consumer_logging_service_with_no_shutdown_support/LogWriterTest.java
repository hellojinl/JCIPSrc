package C7_Cancellation_and_Shutdown.Ch7_13_Producer_consumer_logging_service_with_no_shutdown_support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import support.log.LogCreator;

/**
 * LogWriter正常使用示例
 * 
 * 存在阻塞状态下的运行状态，查看{@link LogWriterTest2 }
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LogWriterTest {

    @Test
    public void normalUse() throws IOException, InterruptedException {
        File file = LogCreator.createLog( "logWriter.log" );

        LogWriter logger = new LogWriter( new FileWriter( file ) );
        logger.start();

        logger.log( "the first line" );
        logger.log( "the seconde line" );
        logger.log( "the third line" );

        System.out.println( "the log content is:" );
        try ( BufferedReader reader = new BufferedReader( new FileReader( file ) ) ) {
            String line = null;
            while ( (line = reader.readLine()) != null ) {
                System.out.println( line );
            }
        }
    }

}
