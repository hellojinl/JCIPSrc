package C6_Task_Execution.C6_4_Web_server_using_a_thread_pool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.support.Client;
import C6_Task_Execution.support.RequestHandler;
import support.sleep.Sleep;

public class TaskExecutionWebServerTest {

    private static final int MAX_CLIENT_COUNT = 10;
    private static final int PORT = 8130;

    private static final ExecutorService serverPool = Executors.newSingleThreadExecutor();
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

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

        private static final int NTHREADS = 100;
        private final Executor exec = Executors.newFixedThreadPool( NTHREADS );

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
                            } catch ( IOException e ) {
                                e.printStackTrace();
                            }
                        }
                    };
                    exec.execute( task );
                } catch ( IOException e1 ) {
                    e1.printStackTrace();
                }

            }
        }

        private void handleRequest(Socket connection) throws IOException {
            RequestHandler.handleRequest( connection );
        }
    }
}
