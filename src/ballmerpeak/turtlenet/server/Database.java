package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import java.util.Vector;
import java.security.*;
import java.sql.*;
import java.security.*;

//NB: My initial version is crude, inefficient, and not even a database. It
//    exists solely to allow me to write other parts of the system. While the
//    methods are useful, their implementation should be rewritten in almost all
//    cases.

public class Database {
    public Database (String location) {
        path    = location;
        posts   = new Vector<Pair<String, Message>>();
        claims  = new Vector<Message>();
        friends = new Vector<Friend>();
	dbConnection = null;

	dbConnect();
        
        addFriend(Crypto.getPublicKey());
    }

    //Connects to a pre-defined database
    public void dbConnect() {

	try {
	    Class.forName("org.sqlite.JDBC");
	    dbConnection = DriverManager.getConnection("jdbc:sqlite:"
						   + path + "/turtlenet.db");
	    //so the DB doesn't make changes when we don't want it to
	    dbConnection.setAutoCommit(false);

	//don't quite know what to make the program do here
	} catch(Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage() );
	    System.exit(0);
	}

	System.out.println("TurtleNet Database Connected Successfully.");

    }

    //Disconnects the pre-defined database
    public void dbDisconnect() {
	
	try {
	    dbConnection.close();
	
	//same problem here - don't know whether to kill it or let it hang
	} catch(Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage() );
	    System.exit(0);
	}

	System.out.println("TurtleNet Database Disconnected Successfully.");
	
    }
    
    public Vector<Pair<String, Message>> getPosts () {
        return posts;
    }
    
    public Vector<Message> getPostsBy (String name) {
        Vector<Message> msgs = new Vector<Message> ();
        for (int i = 0; i < posts.size(); i++)
            if (posts.get(i).first.equals(name))
                msgs.add(posts.get(i).second);
        return msgs;
    }
    
    public Vector<Friend> getFriends () {
        return friends;
    }
    
    public PublicKey getKey (String name) {
        for (int i = 0; i < friends.size(); i++)
            if (friends.get(i).getName().equals(name))
                return friends.get(i).getKey();
        
        return null;
    }
    
    public String getSignatory (Message m) {
        for (int i = 0; i < friends.size(); i++) {
            if (Crypto.verifySig(m, friends.get(i).getKey()))
                return friends.get(i).getName();
        }
        return "unknown";
    }
    
    public PublicKey getSignatoryKey (Message m) {
        for (int i = 0; i < friends.size(); i++) {
            if (Crypto.verifySig(m, friends.get(i).getKey()))
                return friends.get(i).getKey();
        }
        System.out.println("WARNING: Can't find signatory, about to crash due to not handling this case");
        return null;
    }
    
    public void addPost (Message post) {
        posts.add(new Pair<String, Message>(getSignatory(post), post));
    }
    
    public void addFriend (PublicKey k) {
        friends.add(new Friend(getName(k), k));
    }
    
    public void addClaim (Message claim) {
        claims.add(claim);
        for (int i = 0; i < friends.size(); i++)
            if (friends.get(i).getName().equals("unknown"))
                friends.get(i).setName(getName(friends.get(i).getKey()));
        recalcPostAuthors();
    }
    
    public void addRevocation (Message revocation) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addRevocation(...)");
    }
    
    public void addPdata (Message update) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addPdata(...)");
    }
    
    public void addChat (Message chat) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addChat(...)");
    }
    
    public void addPChat (Message chat) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addPChat(...)");
    }
    
    public void addFPost (Message fpost) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addFPost(...)");
    }
    
    public void addComment (Message comment) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addComment(...)");
    }
    
    public void addLike (Message Like) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addLike(...)");
    }
    
    public void addEvent (Message event) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addEvent(...)");
    }
    
    private void recalcPostAuthors () {
        for (int i = 0; i < posts.size(); i++)
            posts.get(i).first = getSignatory(posts.get(i).second);
    }
    
    public String getName (PublicKey k) {
        for (int i = 0; i < claims.size(); i++)
            if (Crypto.verifySig(claims.get(i), k))
                return claims.get(i).getContent();
        return "unknown";
    }
    
    //variable declarations
    private String path; //path to database directory
    Vector<Pair<String, Message>> posts; //<String author, Message m>
    Vector<Message> claims;
    
    private Vector<Friend> friends;

    private Connection dbConnection;
}
