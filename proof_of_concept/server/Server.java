import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

public class Server
{
    public  static String shutdownPassword = "SHUTDOWN 83eea84d472df09f5e64468996fdff0e";
    private static ServerSocket socket;
    private static boolean running = true;
    
    public static void start (int port) {
        Socket incoming;
        Thread t;

        try {
            socket = new ServerSocket(port);

            while (running) {
                incoming = socket.accept();
                t = new Thread(new Session(incoming));
                t.start();
            }
        } catch (Exception e) {
            if (running)
                System.out.println("ERROR: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public static void shutdown() {
        running = false;

        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static void main (String[] argv) {
        start(31415);
    }
}

class Session implements Runnable
{
    private Socket client;
    
    Session (Socket s) {
        client = s;
    }

    // execute()s the clients command and then closes the connection.
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader
              (new InputStreamReader(client.getInputStream()));
            out = new PrintWriter
              (new OutputStreamWriter(client.getOutputStream()));

            execute(in.readLine(), in, out);
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        
        //close everything related to this session
        try {
            in.close();
        } catch (Exception e) {}
            
        try {
            out.close();
        } catch (Exception e) {}
          
        try {
            client.close();
        } catch (Exception e) {}
    }
    
    public void execute(String cmd, BufferedReader in, PrintWriter out) {
        if (cmd.equals(Server.shutdownPassword)) {
            System.out.println("WARNING: shutdown password should be loaded from config file");
            System.out.println("Shutting down");
            Server.shutdown();
        } else {
            System.out.println("recieved \"" + cmd + "\", ignoring it");
            out.write("1");
        }
    }
}
