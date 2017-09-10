package C6_Task_Execution.C6_5_Executor_that_starts_a_new_thread_for_each_task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

/**
 * 使用ThreadPerTaskExecutor将TaskExecutionWebServer修改为类似ThreadPerTaskWebServer的行为
 * （通过更换Executor的实现，改变程序的行为）
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 * 
 * @see C6_Task_Execution.C6_4_Web_server_using_a_thread_pool.TaskExecutionWebServer
 * @see C6_Task_Execution.C6_2_Web_server_that_starts_a_new_thread_for_each_request.ThreadPerTaskWebServer
 */
public class TaskExecutionWebServer2 {

    private static final Executor exec = new ThreadPerTaskExecutor();

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
