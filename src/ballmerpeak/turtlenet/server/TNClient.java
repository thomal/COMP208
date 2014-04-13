package ballmerpeak.turtlenet.server;

import java.util.Date;

public class TNClient implements Runnable {
    static boolean running = true;
    
    public void run () {
        Logger.init("LOG_turtlenet");
        Logger.write("===== Turtlenet started at " + new Date() + "=====");
        NetworkConnection connection    = new NetworkConnection("localhost");
        Thread            networkThread = new Thread(connection);
        Database          db            = new Database("./db");
        
        if (!Crypto.keysExist()) //move into GUI class
            Crypto.keyGen();
        
        networkThread.start(); //download new messages, wait 1 second, repeat
        
        while (running)
            while (connection.hasMessage())
                Parser.parse(Crypto.decrypt(connection.getMessage()), db);
        
        connection.close();
        db.dbDisconnect();
        Logger.write("===== Turtlenet closed  at " + new Date() + "=====");
        Logger.close();
    }
}
