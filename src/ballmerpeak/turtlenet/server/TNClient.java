package ballmerpeak.turtlenet.server;

import java.util.Date;
import java.util.Scanner;

public class TNClient implements Runnable {
    public static boolean running = true;
    
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
        Logger.write("===== Turtlenet started at " + new Date() + "=====");
        NetworkConnection connection    = new NetworkConnection("localhost");
        Thread            networkThread = new Thread(connection);
        Database          db            = new Database();
        
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
