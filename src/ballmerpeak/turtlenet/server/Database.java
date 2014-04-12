package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import java.util.Vector;
import java.security.*;
import java.sql.*;
import java.security.*;
import java.io.File;

public class Database {
    public Database (String location) {
        path    = location;
	dbConnection = null;
	
	File db = new File(path);
	if (!db.exists())
	    if (!dbCreate(path))
	        System.out.println("CRITICAL: Unable to create DB at: " + path);
	
	dbConnect();
    }
    
    //Creates a database from scratch
    public static boolean dbCreate(String path) {
        System.out.println("CRITICAL: Unimplemented method Database.dbCreate(" + path + ")");
        return false;
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
    
    //Get from DB
    public Vector<Message> getPostsBy (PublicKey key) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.getPostsBy(...)");
        return null;
    }
    
    public PublicKey[] getKey (String name) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.getKey(...)");
        return null;
    }
    
    public String getName (PublicKey k) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.getName(...)");
        return null;
    }
    
    public PublicKey getSignatory (Message m) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addSignatory(...)");
        return null;
    }
    
    //Add to DB
    public void addPost (Message post) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addPost(...)");
    }
    
    public void addKey (PublicKey k) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addKey(...)");
    }
    
    public void addClaim (Message claim) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addClaim(...)");
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
    
    public void addMessageToChat (Message msg) {
        //REPLACE ME
        System.out.println("CRITICAL: Unimplemented method Database.addMessageToChat(...)");
    }
    
    /* If you can see an FPOST, it's a request to post it on your wall */
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
    
    //variable declarations
    private String path; //path to database directory
    private Connection dbConnection;
}
