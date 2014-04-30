package ballmerpeak.turtlenet.server;

import java.util.Date;
import java.util.Scanner;

public class TNClient implements Runnable {
    public boolean running = true;
    public NetworkConnection connection;
    public Thread networkThread;
    public Database db = null;
    public String password = "NOT SET";
    
    public TNClient (String pw) {
        password = pw;
    }
    
    public void run () {
        if (!Crypto.keysExist())
            Crypto.keyGen();
        
        connection    = new NetworkConnection("127.0.0.1");
        networkThread = new Thread(connection);
        db            = new Database(password);
        
        networkThread.start();
        
        while (running)
            while (connection.hasMessage())
                Parser.parse(Crypto.decrypt(connection.getMessage()), db);
        
        connection.close();
        db.dbDisconnect();
        Logger.close();
    }
}
