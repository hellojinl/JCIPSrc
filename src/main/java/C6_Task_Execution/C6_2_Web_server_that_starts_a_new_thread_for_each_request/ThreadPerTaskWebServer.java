package C6_Task_Execution.C6_2_Web_server_that_starts_a_new_thread_for_each_request;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ThreadPerTaskWebServer
 * <p/>
 * Web server that starts a new thread for each request
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThreadPerTaskWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket( 80 );
        while ( true ) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                public void run() {
                    handleRequest( connection );
                }
            };
            new Thread( task ).start();
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
