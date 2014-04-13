package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import java.util.Vector;
import java.security.*;
import java.sql.*;
import java.security.*;
import java.io.File;

public class Database {
    public static String path = "./db"; //path to database directory
    private Connection dbConnection;

    public Database () {
	dbConnection = null;
	
	File db = new File(path);
	if (!db.exists())
	    dbCreate();
	else
	    dbConnect(); //this occurs during dbCreate, no need to repeat it
    }
    
    public static boolean DBDirExists() {
        File dir = new File(path);
        return dir.exists();
    }
    
    public static boolean createDBDir() {
        return (new File(path)).mkdirs();
    }
    
    //Creates a database from scratch
    public void dbCreate() {
        try {
            if (!Database.DBDirExists())
                Database.createDBDir();
            Logger.write("CRITICAL: Unimplemented method Database.dbCreate(" + path + ")");
            dbConnect();
            Statement statement = dbConnection.createStatement();
            dbConnection.setAutoCommit(false);
            statement.executeUpdate("CREATE TABLE user (                   " +
                                        "user_id    INT         NOT NULL, " + 
                                        "username   VARCHAR(25) NOT NULL, " +
                                        "name       VARCHAR(30),          " +
                                        "birthday   DATE,                 " + 
                                        "sex        VARCHAR(1),           " + 
                                        "email      VARCHAR(30),          " + 
                                        "public_key VARCHAR(8),           " +
                                        "PRIMARY KEY (user_id)            " +
                                    ");");
            dbConnection.commit();
        } catch (Exception e) {
            Logger.write("CRITICAL: Failed to create databse: " + e);
        }
    }

    //Connects to a pre-defined database
    public boolean dbConnect() {

	try {
	    Class.forName("org.sqlite.JDBC");
	    dbConnection = DriverManager.getConnection("jdbc:sqlite:"
						   + path + "/turtlenet.db");
	    //so the DB doesn't make changes when we don't want it to
	    dbConnection.setAutoCommit(false);

	} catch(Exception e) { //Exception logged to disk, program allowed to crash naturally
	    Logger.write(e.getClass().getName() + ": " + e.getMessage() );
	}

        return true;
    }

    //Disconnects the pre-defined database
    public void dbDisconnect() {
	
	try {
	    dbConnection.close();
	
	} catch(Exception e) { //Exception logged to disk, program allowed to continue
	    Logger.write(e.getClass().getName() + ": " + e.getMessage() );
	}

	Logger.write("TurtleNet Database Disconnected Successfully.");
    }
    
    //Get from DB
    public Vector<Message> getPostsBy (PublicKey key) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.getPostsBy(...)");
        return null;
    }
    
    public PublicKey[] getKey (String name) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.getKey(...)");
        return null;
    }
    
    public String getName (PublicKey k) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.getName(...)");
        return null;
    }
    
    public PublicKey getSignatory (Message m) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addSignatory(...)");
        return null;
    }
    
    //Add to DB
    public void addPost (Message post) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addPost(...)");
    }
    
    public void addKey (PublicKey k) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addKey(...)");
    }
    
    public void addClaim (Message claim) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addClaim(...)");
    }
    
    public void addRevocation (Message revocation) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addRevocation(...)");
    }
    
    public void addPdata (Message update) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addPdata(...)");
    }
    
    public void addChat (Message chat) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addChat(...)");
    }
    
    public void addMessageToChat (Message msg) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addMessageToChat(...)");
    }
    
    /* If you can see an FPOST, it's a request to post it on your wall */
    public void addFPost (Message fpost) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addFPost(...)");
    }
    
    public void addComment (Message comment) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addComment(...)");
    }
    
    public void addLike (Message Like) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addLike(...)");
    }
    
    public void addEvent (Message event) {
        //REPLACE ME
        Logger.write("CRITICAL: Unimplemented method Database.addEvent(...)");
    }
}
