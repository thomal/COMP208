package ballmerpeak.turtlenet.server;

import java.util.Date;
import java.util.Scanner;

public class TNClient implements Runnable {
    public NetworkConnection connection;
    public Thread networkThread;
    public Database db = null;
    public String password = "NOT SET";
    public boolean running = true;
    public boolean dbReady = false;
    
    public TNClient (String pw) {
        password = pw;
    }
    
    public void run () {
        if (!Crypto.keysExist())
            Crypto.keyGen();
        
        connection    = new NetworkConnection("turtle.turtlenet.co.uk");
        networkThread = new Thread(connection);
        db            = new Database(password);
        
        networkThread.start();
        dbReady = true;
        
        while (running)
            while (connection.hasMessage())
                Parser.parse(Crypto.decrypt(connection.getMessage()), db);
        
        connection.close();
        db.dbDisconnect();
        Logger.close();
    }
}
