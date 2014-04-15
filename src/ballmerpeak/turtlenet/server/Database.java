package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
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
        if (db.exists()) dbConnect(); else dbCreate();
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
        Logger.write("INFO", "DB", "Creating database");
        try {
            if (!Database.DBDirExists())
                Database.createDBDir();
            dbConnect();
            for (int i = 0; i < DBStrings.createDB.length; i++)
                execute(DBStrings.createDB[i]);
        } catch (Exception e) {
            Logger.write("FATAL", "DB", "Failed to create databse: " + e);
        }
    }

    //Connects to a pre-defined database
    public boolean dbConnect() {
        Logger.write("INFO", "DB", "Connecting to database");
        try {
            Class.forName("org.sqlite.JDBC");
            dbConnection = DriverManager.getConnection("jdbc:sqlite:db/turtlenet.db");
            return true;
        } catch(Exception e) { //Exception logged to disk, program allowed to crash naturally
            Logger.write("FATAL", "DB", "Could not connect: " + e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }

    //Disconnects the pre-defined database
    public void dbDisconnect() {
        Logger.write("INFO", "DB", "Disconnecting from database");
        try {
            dbConnection.close();
        } catch(Exception e) { //Exception logged to disk, program allowed to continue
            Logger.write("FATAL", "DB", "Could not disconnect: " + e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    
    public void execute (String query) throws Exception {
        if (query.indexOf('(') != -1)
            Logger.write("VERBOSE", "DB", "execute(\"" + query.substring(0,query.indexOf('(')) + "...\")");
        else
            Logger.write("VERBOSE", "DB", "execute(\"" + query.substring(0,20) + "...\")");
        
        Statement statement = dbConnection.createStatement();
        statement.setQueryTimeout(30);
        dbConnection.setAutoCommit(false);
        statement.executeUpdate(query);
        dbConnection.commit();
        dbConnection.setAutoCommit(true);
    }
    
    public ResultSet query (String query) {
        if (query.indexOf('(') != -1)
            Logger.write("VERBOSE", "DB", "execute(\"" + query.substring(0,query.indexOf('(')) + "...\")");
        else
            Logger.write("VERBOSE", "DB", "execute(\"" + query.substring(0,20) + "...\")");
        
        try {
            Statement statement = dbConnection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet r = statement.executeQuery(query);
            return r;
        } catch (Exception e) {
            Logger.write("RED", "DB", "Failed to query database: " + e);
            return null;
        }
    }
    
    //Get from DB
    public Message[] getPostsBy (PublicKey key) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.getPostsBy(...)");
        return null;
    }
    
    public PublicKey[] getKey (String name) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.getKey(...)");
        return null;
    }
    
    public String getName (PublicKey k) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.getName(...)");
        return null;
    }
    
    public PublicKey getSignatory (Message m) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addSignatory(...)");
        return null;
    }
    
    //Add to DB
    public void addPost (Message post) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addPost(...)");
    }
    
    public void addKey (PublicKey k) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addKey(...)");
    }
    
    public void addClaim (Message claim) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addClaim(...)");
    }
    
    public void addRevocation (Message revocation) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addRevocation(...)");
    }
    
    public void addPdata (Message update) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addPdata(...)");
    }
    
    public void addChat (Message chat) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addChat(...)");
    }
    
    public void addMessageToChat (Message msg) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addMessageToChat(...)");
    }
    
    /* If you can see an FPOST, it's a request to post it on your wall */
    public void addFPost (Message fpost) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addFPost(...)");
    }
    
    public void addComment (Message comment) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addComment(...)");
    }
    
    public void addLike (Message Like) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addLike(...)");
    }
    
    public void addEvent (Message event) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addEvent(...)");
    }
}
