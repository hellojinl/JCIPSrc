package C7_Cancellation_and_Shutdown.Ch7_26_Registering_a_shutdown_hook_to_stop_the_logging_service;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import support.TimeUtil;

/**
 * 通过注册一个关闭钩子来停止日志服务
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
        Runtime.getRuntime().addShutdownHook( new Thread() {
            public void run() {
                try {
                    System.out.println( TimeUtil.defaultNow() + " step into shutdownHook" );
                    LogService.this.stop();
                    System.out.println( TimeUtil.defaultNow() + " stopped" );
                } catch ( InterruptedException ignored ) {

                }
            }
        } );
    }

    public void stop() throws InterruptedException {
        try {
            System.out.println( TimeUtil.defaultNow() + " stop start..." );
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
