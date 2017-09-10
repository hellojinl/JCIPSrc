package C6_Task_Execution.C6_8_Web_server_with_shutdown_support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import C6_Task_Execution.C6_5_Executor_that_starts_a_new_thread_for_each_task.ThreadPerTaskExecutor;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class LifecycleWebServerTest {

    private final static int MAX_CLIENT_COUNT = 110;
    private final static int PORT = 7895;
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

    /**
     * @throws InterruptedException
     * 
     */
    @Test
    public void justRunIt() throws InterruptedException {
        Executor executor = new ThreadPerTaskExecutor();
        executor.execute( new Runnable() {

            @Override
            public void run() {
                LifecycleWebServer webServer = new LifecycleWebServer();
                try {
                    webServer.start();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

        } );

        for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
            clientPool.execute( new Client( "localhost", PORT ) );
        }

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 执行2秒

        clientSendMsg( "localhost", PORT, "s" ); // 请求停止exec
        for (int i = 0; i < 10; i++) {
            // 让socket.accept()不断的接受消息，使得有机会调用exec.isShutdown()判断是否退出while循环
            // 否则，将卡在socket.accept()处
            clientPool.execute( new Client( "localhost", PORT ) );
        }

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 等待2秒

    }

    static class LifecycleWebServer {
        private final ExecutorService exec = Executors.newCachedThreadPool();

        /** 请求次数 */
        private final AtomicInteger requestCount = new AtomicInteger();

        public void start() throws IOException {
            System.out.println( "start" );
            ServerSocket socket = new ServerSocket( PORT );
            while ( !exec.isShutdown() ) {
                try {

                    final Socket conn = socket.accept();

                    exec.execute( new Runnable() {
                        public void run() {
                            requestCount.incrementAndGet();
                            try {
                                handleRequest( conn );
                            } catch ( IOException e ) {
                                e.printStackTrace();
                            }
                        }
                    } );
                } catch ( RejectedExecutionException e ) {
                    if (!exec.isShutdown())
                        log( "task submission rejected", e );
                }
            }
            System.out.println( "exec.isShutdown() == true" );
        }

        public void stop() {
            System.out.println( "stop" );
            exec.shutdown();
        }

        private void log(String msg, Exception e) {
            Logger.getAnonymousLogger().log( Level.WARNING, msg, e );
        }

        void handleRequest(Socket connection) throws IOException {
            System.out.println( "handle request" );
            Request req = readRequest( connection );
            if (isShutdownRequest( req ))
                stop();
            else
                dispatchRequest( req );
        }

        interface Request {
        }

        class StopRequest implements Request {

        }

        class TaskRequest implements Request {

        }

        private Request readRequest(Socket connection) throws IOException {
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            String info = br.readLine();
            if ("s".equals( info )) {
                return new StopRequest();
            }
            return new TaskRequest();
        }

        private void dispatchRequest(Request r) {
            System.out.println( "dispatch request" );
        }

        private boolean isShutdownRequest(Request r) {
            return r instanceof StopRequest;
        }
    }

    class Client implements Runnable {

        private final String host;
        private final int port;

        public Client(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            clientSendMsg( this.host, this.port, "msg" );
        }

    }

    private void clientSendMsg(String host, int port, String msg) {
        try {
            Socket socket = new Socket( host, port );

            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter( os );

            pw.write( msg );
            pw.flush();
            socket.shutdownOutput();

            pw.close();
            os.close();
            socket.close();
        } catch ( UnknownHostException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

}
