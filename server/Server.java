import java.io.*;
import java.net.*;
import java.util.Date;
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
        out.flush();
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
    
    //Protocol:
    //NB: The universe came into existance at midnight on january 1st 1970
    //A typical session is the following:
    //    Connect -> Send command to server -> disconnect
    //Valid commands are the following:
    //    t          - request the number of milliseconds since midnight 1970-01-01
    //    s <string> - request that a string be stored on the server
    //    get <long> - get every message posted since <long> number of milliseconds past midnight 1970-01-01
    //Responses are the following:
    //s         - success
    //e         - error
    //<long>    - number of milliseconds since midnight on 1970-01-01
    //<string>* - (0 or more strings) messages requested using get
    public void execute(String cmd, BufferedReader in, PrintWriter out) {
        if (cmd.equals(Server.shutdownPassword)) {
            System.out.println("WARNING: shutdown password should be loaded from config file");
            System.out.println("Shutting down");
            Server.shutdown();
        }
        
        else if (cmd.equals("t")) {
            out.write(String.valueOf(new Date().getTime()) + "\n");
            out.write("s");
        }
        
        else if (cmd.length() > 2 && cmd.substring(0,1).equals("s")) {
            try {
                String message = cmd.substring(2);
                System.out.println("Storing: " + message);
                BufferedWriter writer = new BufferedWriter(
                                    new FileWriter(
                                    new File("./data/" + (new Date()).getTime() +
                                             "_" + Hasher.hash(message))));
                writer.write(message);
                writer.close();
                out.write("s");
            } catch (Exception e) {
                System.out.println("ERROR: Unable to save: " + e);
            }
        }
        
        else if (cmd.length() > 4 && cmd.substring(0,3).equals("get")) {
            try {
                String timestamp = cmd.substring(4);
                long lastRead = Long.parseLong(timestamp);

                File dataDir = new File("./data");
                File[] files = dataDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (lastRead <= getTimestamp(files[i])) {
                        BufferedReader reader = new BufferedReader(
                                                new FileReader(files[i]));
                        String msg = reader.readLine();
                        out.write(getTimestamp(files[i]) + "\\" + msg + "\n");
                    }
                }
                out.write("s");
            } catch (Exception e) {
                System.out.println("ERROR: Cannot execute \"" + cmd + "\"");
                out.write("e");
            }
        }
        
        else {
            System.out.println("recieved \"" + cmd + "\", ignoring it");
            out.write("e");
        }
    }
    
    //44634633434_HASH -> 44634633434
    private long getTimestamp (File f) {
        try {
            String fn = f.getCanonicalPath();
            String ts = fn.substring(fn.lastIndexOf("/")+1, fn.lastIndexOf("_"));
            
            return Long.parseLong(ts);
        } catch (Exception e) {
            System.out.println("ERROR: Could not parse file timestamp: " + e);
        }
        return 1;
    }
}