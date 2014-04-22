package ballmerpeak.turtlenet.server;

import java.util.Date;
import java.util.Scanner;

public class TNClient implements Runnable {
    public boolean running = true;
    public NetworkConnection connection;
    public Thread networkThread;
    public Database db;
    public String password;
    
    public TNClient (String pw) {
        password = pw;
    }
    
    public static void main (String[] argv) {
        TNClient c = new TNClient("password");
        Thread t = new Thread(c);
        t.start();
        
        Scanner s = new Scanner(System.in);
        String l;
        while (c.running) {
            if ((l=s.nextLine()).equals("exit"))
                c.running = false;
            else
                System.out.println(l);
        }
    }
    
    public void run () {
        Logger.init("LOG_turtlenet");
        Logger.write("UNIMPL", "TNClient", "Ignoring password: " + password);
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
