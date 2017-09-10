package C7_Cancellation_and_Shutdown.Ch7_16_Logging_service_that_uses_an_ExecutorService;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用ExecutorService的日志服务
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LogService {

    private final static int TIMEOUT = 5;
    private final static TimeUnit UNIT = TimeUnit.SECONDS;

    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final PrintWriter writer;

    public LogService(Writer writer) {
        this.writer = new PrintWriter( writer, true );
    }

    public void start() {

    }

    public void stop() throws InterruptedException {
        try {
            exec.shutdown();
            exec.awaitTermination( TIMEOUT, UNIT );
        } finally {
            writer.close();
        }
    }

    public void log(String msg) {
        try {
            exec.execute( new WriteTask( msg ) );
        } catch ( RejectedExecutionException ignored ) {
            System.err.println( "Rejected msg = '" + msg + "'" ); // 为了测试
        }
    }

    private class WriteTask implements Runnable {

        private final String msg;

        WriteTask(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            writer.println( this.msg );
        }

    }
}
