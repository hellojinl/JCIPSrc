package C7_Cancellation_and_Shutdown.Ch7_12_Encapsulating_nonstandard_cancellation_in_a_task_with_newTaskFor.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import C6_Task_Execution.support.Client;
import support.PrintUtil;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class SocketUsingTaskTest {

    private static final int PORT = 8343;

    private static final ExecutorService singleThreadServerPool = Executors.newSingleThreadExecutor();
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

    /**
     * RunnableFuture在cancel的时候会调用我们自定义的cancel（里边调用了socket.close()）
     * 
     * @throws IOException
     */
    @Test
    public void justRunIt() throws IOException {
        MyCancellingExecutor executor = new MyCancellingExecutor( 5, 10, 1, TimeUnit.SECONDS,
                new LinkedBlockingQueue< Runnable >() );
        ServerSocket serverSocket = new ServerSocket( PORT );
        singleThreadServerPool.execute( new WebServer( executor, serverSocket ) );

        clientPool.execute( new Client( "localhost", PORT ) );

        Sleep.sleepUninterruptibly( 1, TimeUnit.SECONDS ); // 单元测试，不能设计成永远执行
    }

    /**
     * 统计服务器端接受到的字符数
     *
     */
    class CountTask extends MySocketUsingTask< Integer > {

        protected CountTask(Socket s) {
            super( s );
        }

        public Integer call() throws Exception {
            InputStream is = null;
            BufferedReader br = null;

            try {
                is = this.getSocket().getInputStream();
                br = new BufferedReader( new InputStreamReader( is ) );

                int count = 0;
                String info = null;
                while ( (info = br.readLine()) != null ) {
                    count += info.length();
                }

                return count;
            } catch ( IOException ex ) {
                ex.printStackTrace();
                throw ex;
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch ( IOException ioe ) {
                        ioe.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch ( IOException ioe ) {
                        ioe.printStackTrace();
                    }
                }
            }
        }

    }

    class WebServer implements Runnable {

        private final MyCancellingExecutor exec;
        private final ServerSocket serverSocket;

        WebServer(MyCancellingExecutor exec, ServerSocket serverSocket) {
            this.exec = exec;
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while ( true ) {
                try {
                    final Socket sock = serverSocket.accept();
                    PrintUtil.currentThreadPrintln( "serverSocket.accept()" );

                    PrintUtil.currentThreadPrintln( "exec.submit" );
                    Future< Integer > future = exec.submit( new CountTask( sock ) );

                    taskCancel( future ); // 如果注释掉这行，看到正常运行的结果

                    PrintUtil.currentThreadPrintln( "future.get()" );
                    Integer count = future.get();

                    PrintUtil.currentThreadPrintln( "count = " + count );
                } catch ( CancellationException cancelEx ) {
                    cancelEx.printStackTrace();
                } catch ( IOException | InterruptedException | ExecutionException e ) {
                    e.printStackTrace();
                }
            }
        }

        private void taskCancel(Future< Integer > future) {
            PrintUtil.currentThreadPrintln( "future.cancel(true)" );
            future.cancel( true );
        }

    }

}
