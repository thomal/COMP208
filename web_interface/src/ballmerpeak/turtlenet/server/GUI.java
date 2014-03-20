//NB: The GUI should be very careful when allowing the user to select a friend
//    by their username, the server may be malicious and allow duplicate names.
//    if a duplicate name exists, the user should be informed. Users may be
//    uniquely identified by their public keys.
package ballmerpeak.turtlenet.server;

import java.util.Scanner;
import java.util.Vector;
import java.security.PublicKey;
import java.util.concurrent.Semaphore;

class GUI implements Runnable {
    public GUI (Database db, NetworkConnection nc) {
        database    = db;
        connection  = nc;
        running     = true;
        runningLock = new Semaphore(1);
    }
    
    public void run () {
        while (isRunning())
            update();
    }
    
    public void close () {
    }
    
    public boolean isRunning () {
        boolean r = false;
        try {
            runningLock.acquire();
            r = running;
            runningLock.release();
        } catch (Exception e) {
            System.out.println("WARNING: Acquire interrupted: " + e);
        }
        return r;
    }
    
    private void update () {
        //get input
        System.out.print(">");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        
        //parse input
        //READ
        if (input.equals("READ")) {
            System.out.println("Private messages:");
            Vector<Friend> friends = database.getFriends();
            for (int i = 0; i < friends.size(); i++) {
                Vector<Message> msgs = database.getPostsBy(friends.get(i).getName());
                System.out.println("Messages from " + friends.get(i).getName());
                for (int j = 0; j < msgs.size(); j++)
                    if (msgs.get(j).getCmd().equals("POST"))
                        System.out.println("\t" + msgs.get(j).getContent());
            }
        }
        
        //QUIT
        else if (input.equals("QUIT")) {
            try {
                runningLock.acquire();
                running = false;
                runningLock.release();
            } catch (Exception e) {
                System.out.println("WARNING: Acquire interrupted: " + e);
            }
        }
        
        //POST
        //This doesn't handle multiple firends having the same username
        else if (input.length() > 5 && input.substring(0,5).equals("POST ")) {
            System.out.print("Enter name of recipient:");
            PublicKey key = database.getKey(in.nextLine());
            if (key != null)
                connection.postMessage(input.substring(5), key);
            else
                System.out.println("ERROR: No such friend");
        }
        
        //LIST
        else if (input.equals("LIST")) {
            Vector<Friend> friends = database.getFriends();
            System.out.println("Known friends:");
            for (int i = 0; i < friends.size(); i++)
                System.out.println("\t" + friends.get(i).getName());
        }
        
        //SHOWKEY
        else if (input.equals("SHOWKEY")) {
                System.out.println("Your public key is below, you should give this to your friends." +
                                   " Please ensure you copy/paste it correctly.\n" +
                                   Crypto.encodeKey(Crypto.getPublicKey()));
        }
        
        //ADDKEY
        else if (input.length() > 7 && input.substring(0,7).equals("ADDKEY ")) {
            PublicKey friendsKey = Crypto.decodeKey(input.substring(7));
            if (friendsKey != null)
                database.addFriend(friendsKey);
            else
                System.out.println("Sorry, you seem to have mistyped the key");
        }
        
        //CLAIM
        else if (input.equals("CLAIM")) {
            System.out.print("Enter desired username (publically vsisible): ");
            if (connection.claimName(in.nextLine()))
                System.out.println("Succes.");
            else
                System.out.println("Sorry, that name is taken");
        }
                
        //Invalid command
        else {
            System.out.println("INVALID COMMAND\n\nYou may \"READ\", \"LIST\"," +
                               " \"QUIT\", \"SHOWKEY\", \"ADDKEY <key>\", " +
                               " \"CLAIM\" or \"POST <text>\"");
        }
    }
    
    Database          database;
    NetworkConnection connection;
    boolean           running;
    Semaphore         runningLock;
}
