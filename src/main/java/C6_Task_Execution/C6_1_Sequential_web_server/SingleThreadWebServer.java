package C6_Task_Execution.C6_1_Sequential_web_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SingleThreadWebServer
 * <p/>
 * Sequential web server
 *
 * @author Brian Goetz and Tim Peierls
 */
public class SingleThreadWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket( 80 );
        while ( true ) {
            Socket connection = socket.accept();
            handleRequest( connection );
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
