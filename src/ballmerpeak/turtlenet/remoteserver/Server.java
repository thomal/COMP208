package ballmerpeak.turtlenet.remoteserver;

import ballmerpeak.turtlenet.shared.Message;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.StringTokenizer;
import javax.xml.bind.DatatypeConverter;

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
        System.out.println("Server running...");
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
        System.out.println("Connection from " + client.getInetAddress().getHostAddress());
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
    //    c <claim message> - claim a username UNENCRYPTED PUBLICALLY KNOWN
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
            out.println(String.valueOf(new Date().getTime()));
            out.println("s");
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
                out.println("s");
            } catch (Exception e) {
                System.out.println("ERROR: Unable to save: " + e);
            }
        }
        
        else if (cmd.length() > 4 && cmd.substring(0,3).equals("get")) {
            System.out.println(cmd);
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
                        out.println(msg);
                    }
                }
                out.println("s");
            } catch (Exception e) {
                System.out.println("ERROR: Cannot execute \"" + cmd + "\"");
                out.println("e");
            }
        }
        
        else if (cmd.length() > 2 && cmd.substring(0,2).equals("c ")) {
            Message claim = Message.parse(
                                new String(
                                    DatatypeConverter.parseBase64Binary(
                                        cmd.substring(2))));
            
            String content = claim.getContent();
            File data = new File("./data/" + (new Date()).getTime() + "_" + content);
            if(userExists(content)) {
                out.println("e");
            } else {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(data));
                    writer.write(cmd);
                    writer.close();
                    out.println("s");
                } catch (Exception e) {
                    System.out.println("ERROR: Could not write claim to disk");
                    out.println("e");
                }
            }
        }
        
        else {
            System.out.println("Recieved \"" + cmd + "\", ignoring it");
            out.println("e");
        }
        
        out.flush();
    }
    
    //44634633434_HASH -> 44634633434
    private long getTimestamp (File f) {
        try {
            String fn = f.getName();
            if (fn.indexOf("_") != -1) {
                String ts = fn.substring(0, fn.indexOf("_"));
                return Long.parseLong(ts);
            }
        } catch (Exception e) {
            System.out.println("ERROR: Could not parse file timestamp: " + e);
        }
        return 1;
    }
    
    private Boolean userExists (String name) {
        File dir = new File("./data");
        File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().indexOf("_") != -1) {
                    String fname = files[i].getName();
                    String[] tokens = new String[2];
                    StringTokenizer tokenizer = new StringTokenizer(fname, "_", false);
                    tokens[0] = tokenizer.nextToken();
                    tokens[1] = tokenizer.nextToken();
                    if (tokens[1].equals(name))
                        return true;
                }
            }
        return false;
    }
}
