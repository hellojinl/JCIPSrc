package C6_Task_Execution.C6_1_Sequential_web_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.support.Client;
import C6_Task_Execution.support.RequestHandler;
import support.TimeUtil;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 * @see <a href="http://blog.csdn.net/qq7342272/article/details/9698413">Java
 *      Socket接收和发送（字符串）</a>
 */
public class SingleThreadWebServerTest {

    private static final int MAX_CLIENT_COUNT = 5;
    private static final int PORT = 80;
    private static final ExecutorService singleThreadServerPool = Executors.newSingleThreadExecutor();
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

    /**
     * 输出结果：先单独看服务器线程，它的执行序列是顺序依次执行的（accept... -> receive... ->
     * send...），再看客户端线程，每个线程中的执行序列也是顺序依次执行的（send... -> the replly is
     * ...），但是它们之间的顺序是随机的。
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
        singleThreadServerPool.execute( new SingleThreadServer( serverSocket ) );

        for (int i = 0; i < MAX_CLIENT_COUNT; i++) {
            clientPool.execute( new Client( "localhost", PORT ) );
        }

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 单元测试，不能设计成永远执行
    }

    class SingleThreadServer implements Runnable {

        private final ServerSocket serverSocket;

        SingleThreadServer(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            System.out.println( "the id of the server's thread is [" + Thread.currentThread().getId() + "]" );
            while ( true ) {
                try {
                    System.out.println(
                            TimeUtil.defaultNow() + " [" + Thread.currentThread().getId() + "] server accept..." );
                    Socket connection = serverSocket.accept();
                    handleRequest( connection );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

        }

        private void handleRequest(Socket connection) throws IOException {
            RequestHandler.handleRequest( connection );
        }

    }

}
