import java.util.Scanner;
import java.util.Vector;
import java.security.PublicKey;

class GUI {
    public GUI (Database db, NetworkConnection nc) {
        database = db;
        connection = nc;
    }
    
    public Boolean update () {
        //get input
        System.out.print(">");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        
        //parse input
        //READ
        if (input.equals("READ")) {
            System.out.println("Friends walls:");
            Vector<Friend> friends = database.getFriends();
            for (int i = 0; i < friends.size(); i++) {
                Vector<Message> msgs = database.getPostsBy(friends.get(i).getName());
                if (msgs.size() > 0)
                    System.out.println(friends.get(i).getName());
                for (int j = 0; j < msgs.size(); j++)
                    System.out.println("\t" + msgs.get(j).getContent());
            }
            
            System.out.println("Private chats:");
            
        } else
        
        //QUIT
        if (input.equals("QUIT")) {
            return false;
        } else
        
        //POST
        if (input.length() > 5 && input.substring(0,5).equals("POST ")) {
            System.out.print("Enter name of recipient:");
            PublicKey key = database.getKey(in.nextLine());
            if (key != null)
                connection.postMessage(input.substring(5), key);
            else
                System.out.println("ERROR: No such friend");
        }
        
        //LIST
        if (input.equals("LIST")) {
            Vector<Friend> friends = database.getFriends();
            System.out.println("Known friends:");
            for (int i = 0; i < friends.size(); i++)
                System.out.println("\t" + friends.get(i).getName());
        }
        
        //Invalid command
        else {
            System.out.println("\nYou may \"READ\", \"LIST\", \"QUIT\", or \"POST texthere\"");
        }
            
       return true;
    }
    
    public void close () {
    }
    
    Database database;
    NetworkConnection connection;
}
