package C6_Task_Execution.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import support.TimeUtil;

public final class RequestHandler {

    private RequestHandler() {
    }

    public static void handleRequest(Socket connection) throws IOException {
        try {
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
            OutputStream os = connection.getOutputStream();
            PrintWriter pw = new PrintWriter( os );
            String info = null;
            while ( !((info = br.readLine()) == null) ) {
                System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId()
                        + "] server receives a client msg: '" + info + "'" );
            }
            String reply = "welcome, the server id is " + Thread.currentThread().getId();
            System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId()
                    + "] server sends a reply: '" + reply + "'" );
            pw.write( reply );
            pw.flush();

            pw.close();
            os.close();
            br.close();
            is.close();
        } finally {
            connection.close();
        }
    }
}
