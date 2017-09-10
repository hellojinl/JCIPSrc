package C6_Task_Execution.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import support.TimeUtil;

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

            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader( new InputStreamReader( is ) );

            String msg = "hello, client is " + Thread.currentThread().getId();
            pw.write( msg );
            System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId()
                    + "] client sends a msg: '" + msg + "'" );
            pw.flush();
            socket.shutdownOutput();

            String reply = null;
            while ( !((reply = br.readLine()) == null) ) {
                System.out.println( TimeUtil.defaultNow() + " [" + Thread.currentThread().getId()
                        + "] client receives a reply '" + reply + "'" );
            }

            br.close();
            is.close();
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
