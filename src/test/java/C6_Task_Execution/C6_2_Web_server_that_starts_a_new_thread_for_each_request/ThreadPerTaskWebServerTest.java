package C6_Task_Execution.C6_2_Web_server_that_starts_a_new_thread_for_each_request;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.support.Client;
import C6_Task_Execution.support.RequestHandler;
import support.sleep.Sleep;

public class ThreadPerTaskWebServerTest {

    private static final int MAX_CLIENT_COUNT = 5;
    private static final int PORT = 8123;

    private static final ExecutorService serverPool = Executors.newSingleThreadExecutor();
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

    /**
     * 对于每个客户请求，服务器端都会启动一个线程处理并响应它，它们之间满足如下执行关系 1.client sends a msg 2.server
     * receives a client msg 3.server sends a reply 4.client receives a reply
     * 
     * 另外，服务线程之间的执行顺序是无法预知的，客户端之间的执行顺序是无法预知的
     */
    @Test
    public void justRunIt() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket( PORT );
        } catch ( IOException e ) {
            e.printStackTrace();
            return;
        }
        serverPool.execute( new WebServer( serverSocket ) );

        for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
            clientPool.execute( new Client( "localhost", PORT ) );
        }

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 单元测试，不能设计成永远执行
    }

    class WebServer implements Runnable {

        private final ServerSocket serverSocket;

        WebServer(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while ( true ) {
                try {
                    final Socket connection = serverSocket.accept();

                    Runnable task = new Runnable() {
                        public void run() {
                            try {
                                handleRequest( connection );
                            } catch ( IOException ioe ) {
                                ioe.printStackTrace();
                            }
                        }
                    };
                    new Thread( task ).start();
                } catch ( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        }

        private void handleRequest(Socket connection) throws IOException {
            RequestHandler.handleRequest( connection );
        }
    }

}
