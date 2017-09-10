package C7_Cancellation_and_Shutdown.Ch7_16_Logging_service_that_uses_an_ExecutorService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.log.LogCreator;
import support.sleep.Sleep;

public class LogServiceTest {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool( 1 );

    @Test
    public void JustRunIt() throws IOException {
        File file = LogCreator.createLog( "logService.log" );

        final LogService logger = new LogService( new FileWriter( file ) );
        logger.start();

        cancelExec.schedule( new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println( "logger.stop()" );
                    logger.stop();
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

        }, 1, TimeUnit.SECONDS );

        for (int i = 0; i < 20; i++) {
            String msg = "msg" + i;
            System.out.println( "log msg: '" + msg + "'" );
            logger.log( msg );
            Sleep.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
        }

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS );
    }
}
