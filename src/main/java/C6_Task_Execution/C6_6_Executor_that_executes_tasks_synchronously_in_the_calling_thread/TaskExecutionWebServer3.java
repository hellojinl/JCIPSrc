package C6_Task_Execution.C6_6_Executor_that_executes_tasks_synchronously_in_the_calling_thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

/**
 * 使用WithinThreadExecutor将TaskExecutionWebServer修改为类似SingleThreadWebServer的行为
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 * @see C6_Task_Execution.C6_4_Web_server_using_a_thread_pool.TaskExecutionWebServer
 * @see C6_Task_Execution.C6_1_Sequential_web_server.SingleThreadWebServer
 */
public class TaskExecutionWebServer3 {

    private static final Executor exec = new WithinThreadExecutor();

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket( 80 );
        while ( true ) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                public void run() {
                    handleRequest( connection );
                }
            };
            exec.execute( task );
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
