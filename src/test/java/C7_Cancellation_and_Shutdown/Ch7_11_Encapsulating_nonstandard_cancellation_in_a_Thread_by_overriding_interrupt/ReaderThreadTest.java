package C7_Cancellation_and_Shutdown.Ch7_11_Encapsulating_nonstandard_cancellation_in_a_Thread_by_overriding_interrupt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import support.TimeUtil;
import support.sleep.Sleep;

/**
 * 
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ReaderThreadTest {

    private static final int PORT = 8323;

    private static final ExecutorService serverPool = Executors.newSingleThreadExecutor();
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();

    /**
     * 结果具有一定的随机性，多试几次有机会出现，如下异常 java.net.SocketException: Socket closed
     * 这是因为在in.read的时候，socket已经被关闭了
     */
    @Test
    public void test() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket( PORT );
        } catch ( IOException e ) {
            e.printStackTrace();
            return;
        }
        serverPool.execute( new WebServer( serverSocket ) );

        clientPool.execute( new Client( "localhost", PORT ) ); // 发送消息

        Sleep.sleepUninterruptibly( 2, TimeUnit.SECONDS ); // 单元测试，不能设计成永远执行
    }

    static class WebServer implements Runnable {

        private final ServerSocket serverSocket;

        WebServer(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while ( true ) {
                try {
                    final Socket socket = serverSocket.accept();
                    ReaderThread readerThread = new ReaderThread( socket ); // 仅仅为了测试，对每个请求都建立一个线程肯定是不好的，这里忽略这些
                    readerThread.start();
                } catch ( IOException ex ) {
                    ex.printStackTrace();
                }
            }
        }

    }

    static class ReaderThread extends Thread {
        private static final int BUFSZ = 512;
        private final Socket socket;
        private final InputStream in;

        public ReaderThread(Socket socket) throws IOException {
            this.socket = socket;
            this.in = socket.getInputStream();
        }

        public void interrupt() {
            try {
                socket.close();
                System.out.println(
                        TimeUtil.defaultNow() + " [" + Thread.currentThread().getId() + "] the socket has closed" );
            } catch ( IOException ignored ) {
                ignored.printStackTrace();
            } finally {
                super.interrupt();
            }
        }

        public void run() {
            try {
                byte[] buf = new byte[ BUFSZ ];
                new Thread( new Runnable() {

                    @Override
                    public void run() {
                        Sleep.sleepUninterruptibly( 5, TimeUnit.MILLISECONDS );
                        ReaderThread.this.interrupt();
                    }
                } ).start();
                while ( true ) {
                    int count = in.read( buf );
                    if (count < 0)
                        break;
                    else if (count > 0)
                        processBuffer( buf, count );
                }
            } catch ( IOException e ) { /* Allow thread to exit */
                e.printStackTrace();
            }
        }

        public void processBuffer(byte[] buf, int count) {
            String msg = new String( buf );
            System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId()
                    + "] server receives a client msg(length=" + count + "): '" + msg + "'" );
        }
    }

    public class Client implements Runnable {

        private final String host;
        private final int port;

        public Client(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket( this.host, this.port );

                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter( os );

                String msg = "hellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"
                        + "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello";
                pw.write( msg );
                System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId()
                        + "] client sends a msg(length=" + msg.length() + "): '" + msg + "'" );
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

}
