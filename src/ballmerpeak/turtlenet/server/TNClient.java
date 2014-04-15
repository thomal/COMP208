package ballmerpeak.turtlenet.server;

import java.util.Date;
import java.util.Scanner;

public class TNClient implements Runnable {
    public static boolean running = true;
    public NetworkConnection connection;
    public Thread networkThread;
    public Database db;
    
    public static void main (String[] argv) {
        TNClient c = new TNClient();
        Thread t = new Thread(c);
        t.start();
        
        Scanner s = new Scanner(System.in);
        String l;
        while (TNClient.running) {
            if ((l=s.nextLine()).equals("exit"))
                TNClient.running = false;
            else
                System.out.println(l);
        }
    }
    
    public void run () {
        Logger.init("LOG_turtlenet");
        connection    = new NetworkConnection("localhost");
        networkThread = new Thread(connection);
        db            = new Database();
        
        if (!Crypto.keysExist()) //move into GUI
            Crypto.keyGen();
        networkThread.start();
        
        while (running)
            while (connection.hasMessage())
                Parser.parse(Crypto.decrypt(connection.getMessage()), db);
        
        connection.close();
        db.dbDisconnect();
        Logger.close();
    }
}
