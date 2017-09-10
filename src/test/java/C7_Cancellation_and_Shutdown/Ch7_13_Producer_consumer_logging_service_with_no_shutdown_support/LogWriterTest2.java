package C7_Cancellation_and_Shutdown.Ch7_13_Producer_consumer_logging_service_with_no_shutdown_support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.log.LogCreator;
import support.sleep.Sleep;

/**
 * LogWriter在阻塞情况下的运行状态
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LogWriterTest2 {

    /**
     * 使用LogWriter2，测试当生产者速度大于消费者速度时，生产者将阻塞影响调用者的运行效率
     */
    @Test
    public void lowSpeed() throws IOException, InterruptedException, ExecutionException {
        File file = LogCreator.createLog( "logWriter2.log" );

        final LogWriter2 logger = new LogWriter2( new FileWriter( file ) );
        logger.start();

        ExecutorService exec = Executors.newCachedThreadPool();
        List< WriteLogTask > tasks = new ArrayList< WriteLogTask >();
        for (int i = 0; i < 10; i++) {
            tasks.add( new WriteLogTask( logger ) ); // 10个写日志线程，每个写150条日志
        }
        List< Future< Long > > futures = exec.invokeAll( tasks );

        long totalTimeMillis = 0;
        for (Future< Long > f : futures) {
            Long logTimeMillis = f.get();
            totalTimeMillis += logTimeMillis;
        }

        long avgTimeNanos = TimeUnit.MILLISECONDS.toNanos( totalTimeMillis / 1500 );
        System.out.println( "[ 存在阻塞]10个线程写日志，每个线程写150条，总时间为 = " + totalTimeMillis + " millisecond" );
        System.out.println( "[ 存在阻塞] 写一条日志花费时间 = " + avgTimeNanos + " nanosecond" );

        Sleep.sleepUninterruptibly( 10, TimeUnit.MILLISECONDS ); // 消除阻塞

        long startTimeNanos = System.nanoTime();
        logger.log( Thread.currentThread().getName() + " writes a msg." );
        long timeNanos = System.nanoTime() - startTimeNanos;

        System.out.println( "[不存在阻塞] 写一条日志花费时间 = " + timeNanos + " nanosecond " );
        System.out.println( "[阻塞状态运行时间]/[不阻塞状态运行时间] = " + (avgTimeNanos / timeNanos) + " 倍" );

        // 结论：
        // 1.写日志方法可能阻塞调用线程
        // 2.写日志服务无法关闭（因为没有提供关闭方法）
    }

    class WriteLogTask implements Callable< Long > {

        private final LogWriter2 logWriter;

        WriteLogTask(LogWriter2 logWriter) {
            this.logWriter = logWriter;
        }

        @Override
        public Long call() throws Exception {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 150; i++) {
                logWriter.log( Thread.currentThread().getName() + " writes a msg." );
            }
            return System.currentTimeMillis() - startTime;
        }

    }

    class LogWriter2 {
        private final BlockingQueue< String > queue;
        private final LoggerThread logger;
        private static final int CAPACITY = 1000;

        public LogWriter2(Writer writer) {
            this.queue = new LinkedBlockingQueue< String >( CAPACITY );
            this.logger = new LoggerThread( writer );
        }

        public void start() {
            logger.start();
        }

        public void log(String msg) throws InterruptedException {
            queue.put( msg );
        }

        private class LoggerThread extends Thread {
            private final PrintWriter writer;

            public LoggerThread(Writer writer) {
                this.writer = new PrintWriter( writer, true ); // autoflush
            }

            public void run() {
                try {
                    while ( true ) {
                        writer.println( queue.take() );
                        Sleep.sleepUninterruptibly( 10, TimeUnit.MILLISECONDS ); // 放慢消费速度
                    }
                } catch ( InterruptedException ignored ) {
                } finally {
                    writer.close();
                }
            }
        }
    }
}
