package C7_Cancellation_and_Shutdown.Ch7_15_Adding_reliable_cancellation_to_LogWriter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.annotations.GuardedBy;
import support.log.LogCreator;
import support.sleep.Sleep;

public class LogServiceTest {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool( 1 );

    @Test
    public void test() throws IOException, InterruptedException {
        File file = LogCreator.createLog( "logService.log" );

        final LogService logger = new LogService( new FileWriter( file ) );
        logger.start();

        cancelExec.schedule( new Runnable() {
            public void run() {
                logger.stop();
            }
        }, 1, TimeUnit.SECONDS );

        boolean result = false;
        for (int i = 0; i < 20; i++) {
            String msg = "msg" + i;
            try {
                logger.log( msg );
            } catch ( IllegalStateException ex ) {
                result = true;
            }
            System.out.println( "log msg: '" + msg + "'" );
            Sleep.sleepUninterruptibly( 100, TimeUnit.MILLISECONDS );
        }

        logger.join();
        assertTrue( result );
    }

    class LogService {

        private final BlockingQueue< String > queue;
        private final LoggerThread loggerThread;
        private final PrintWriter writer;
        @GuardedBy("this")
        private boolean isShutdown;
        @GuardedBy("this")
        private int reservations;

        public LogService(Writer writer) {
            this.queue = new LinkedBlockingQueue< String >();
            this.loggerThread = new LoggerThread();
            this.writer = new PrintWriter( writer );
        }

        public void start() {
            loggerThread.start();
        }

        public void stop() {
            synchronized ( this ) {
                isShutdown = true;
            }
            loggerThread.interrupt();
        }

        public void join() throws InterruptedException {
            loggerThread.join();
        }

        public void log(String msg) throws InterruptedException {
            synchronized ( this ) {
                if (isShutdown)
                    throw new IllegalStateException( /* ... */ );
                ++reservations;
            }
            queue.put( msg );
        }

        private class LoggerThread extends Thread {
            public void run() {
                try {
                    while ( true ) {
                        try {
                            synchronized ( LogService.this ) {
                                if (isShutdown && reservations == 0)
                                    break;
                            }
                            String msg = queue.take();
                            System.out.println( "write msg: '" + msg + "', isShutDown = " + isShutdown
                                    + ", reservations = " + reservations );
                            synchronized ( LogService.this ) {
                                --reservations;
                            }
                            writer.println( msg );

                            Sleep.sleepUninterruptibly( 500, TimeUnit.MILLISECONDS );
                        } catch ( InterruptedException e ) { /* retry */
                            e.printStackTrace();
                        } catch ( RuntimeException re ) {
                            // 忽略sleepNonInterruptible被中断的异常
                            re.printStackTrace();
                        }
                    }
                } finally {
                    writer.close();
                }
            }
        }
    }

}
