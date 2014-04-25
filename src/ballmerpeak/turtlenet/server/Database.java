/* FOR MIKES ATTENTION:
 * String[][] demo = {{"a", "b"}, {"c", "d"}, {"e", "f"}}
 * is a String[3][2]
 * An array of 3 String[2]'s
 * Each String[2] contains two strings [0] and [1]
 * So demo[1][1] = "d"
 *    demo[2][0] = "e"
 * This format is used to describe return formats. Demos can be found in
 *    TurtlenetImpl.java in many cases.
*/

package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.shared.Conversation;
import java.security.*;
import java.sql.*;
import java.security.*;
import java.util.List;
import java.io.File;
import java.util.Vector;

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
    
    public void execute (String query) {
        try {
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
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
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
    public String getPDATA(String field, PublicKey key) {
        String value = "EXCEPTION IN GETPDATA";
        try {
            String strKey = Crypto.encodeKey(key);
            String sqlStatement  = DBStrings.getPDATA.replace("__FIELD__", field);
            sqlStatement = sqlStatement.replace("__KEY__", strKey); //mods SQL template

            ResultSet results = query(sqlStatement);
            results.beforeFirst();
            if(results.next() )
                value = results.getString(field); //gets current value in 'field'
            else
                value = "<No Value>";
            Logger.write("VERBOSE", "DB", "Called method Database.getPDATA(" + field + ",...) = " + value); 
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return value;
    }
    
    //Set the CMD to POST in the Message constructor
    public Message[] getWallPost (PublicKey key) {
        Vector<Message> posts = new Vector<Message>();
        try {
            String sqlStatement  = DBStrings.getWallPostIDs.replace("__KEY__", Crypto.encodeKey(key) );
            ResultSet results = query(sqlStatement);
            results.beforeFirst();
        
            while (results.next() ) {
                Vector<String> visibleTo = new Vector<String>();
                ResultSet currentPost = query(DBStrings.getPost.replace("__ID__", Integer.toString(results.getInt("postID") ) ) );
                ResultSet currentPostVisibleTo = query(DBStrings.getVisibleTo.replace("__ID__", Integer.toString(results.getInt("postID"))));
                currentPostVisibleTo.beforeFirst();
                while(currentPostVisibleTo.next())
                    visibleTo.add(currentPostVisibleTo.getString("key") );

                Message m = new MessageFactoryImpl().newPOST(currentPost.getString("msgText"), currentPost.getString("recieverKey"), ((String[])visibleTo.toArray()) );
                m.timestamp = Long.parseLong(currentPost.getString("time"));
                m.signature = currentPost.getString("sig");
                m.command = "POST";
                posts.add(m);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        Logger.write("VERBOSE", "DB", "getWallPost(...)");
        return (Message[])posts.toArray();
    }
    
    //Return all conversations
    public Conversation[] getConversations () {
        Vector<Conversation> convoList = new Vector<Conversation>();
        ResultSet convoSet = query(DBStrings.getConversations);

        try {
            convoSet.beforeFirst();
            while (convoSet.next())
                convoList.add(getConversation(convoSet.getString("convoID")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        Logger.write("VERBOSE", "DB", "getConversation()");
        return (Conversation[])convoList.toArray();
    }
    
    //Get keys of all people in the given conversation
    public PublicKey[] getPeopleInConvo (String sig) {
        Vector<PublicKey> keys = new Vector<PublicKey>();
        ResultSet keySet = query(DBStrings.getConversationMembers.replace("__SIG__", sig));
        
        try {
            keySet.beforeFirst();
            while (keySet.next())
                keys.add(Crypto.decodeKey(keySet.getString("key")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        Logger.write("VERBOSE", "DB", "getPeopleInConvo(...)");
        return (PublicKey[])keys.toArray();
    }
    
    //Reurn a conversation object
    public Conversation getConversation (String sig) {
        try {
            ResultSet convoSet = query(DBStrings.getConversation.replace("__SIG__", sig));
            convoSet.beforeFirst();
            if(convoSet.next() ) {
                String timestamp = convoSet.getString("timeCreated");
                ResultSet messages = query(DBStrings.getConversationMessages.replace("__SIG__", sig));
                String firstMsg;
                messages.beforeFirst();
                if (messages.next())
                    firstMsg = messages.getString("msgText");
                else
                    firstMsg = "<no messages yet>";
                PublicKey[] keys = getPeopleInConvo(sig);
                String[] keystrings = new String[keys.length];
                String[] users = new String[keys.length];
                for (int i = 0; i < keys.length; i++) {
                    keystrings[i] = Crypto.encodeKey(keys[i]);
                    users[i] = getName(keys[i]);
                }
                Logger.write("VERBOSE", "DB", "getConversation(...)");    
                return new Conversation(sig, timestamp, firstMsg, users, keystrings);
            } else {
                Logger.write("ERROR", "DB", "getConversation(...) passed invalid Signature.");    
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        return new Conversation();
    }
    
    //Return all messages in a conversation
    //{{username, time, msg}, {username, time, msg}, etc.}
    //Please order it so that element 0 is the oldest message
    public String[][] getConversationMessages (String sig) {
        ResultSet messageSet = query(DBStrings.getConversationMessages.replace("__SIG__", sig));
        Vector<String[]> messagesList = new Vector<String[]>();
        
        try {
            messageSet.beforeFirst();
            while(messageSet.next() ) {
                String[] message = new String[3];
                message[0] = getName(Crypto.decodeKey(messageSet.getString("sendersKey")));
                message[1] = messageSet.getString("time");
                message[2] = messageSet.getString("msgText");
                
                messagesList.add(message);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        Logger.write("VERBOSE", "DB", "getConversationMessages(...)");
        return (String[][])messagesList.toArray();
    }
    
    //If multiple people have the same username then:
    //Logger.write("FATAL", "DB", "Duplicate usernames");
    //System.exit(1);
    public PublicKey getKey (String userName) {
        int nameCount = 0;
        String key = "<No Key>";
        ResultSet results = query(DBStrings.getKey.replace("__USERNAME__", userName) );
        
        try {
            results.beforeFirst();
            while(results.next()) {
                nameCount++;
                key = results.getString("key");
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        if(nameCount == 0)
            Logger.write("ERROR", "DB", "getKey(" +  userName + ") - No keys found for userName");
        else if (nameCount > 1 )
            Logger.write("ERROR", "DB", "getKey(" + userName + ") - Multple userNames found for key; Server OPs are evil!");

        Logger.write("VERBOSE", "DB", "getKey(" + userName + ")");
        return Crypto.decodeKey(key);
    }
    
    //Return the name of each member and if it can see your profile info
    //In this format: {{"friends", "false"}, {"family", "true"}, etc.}
    public String[][] getCategories () {
        Vector<String[]> catList = new Vector<String[]>();
        String catName;
        String canSeePDATA;
        ResultSet categorySet = query(DBStrings.getCategories);
        
        try {
            categorySet.beforeFirst();
            while(categorySet.next() ) {
                String[] category = new String[2];
                category[0] = categorySet.getString("catID");
                category[1] = categorySet.getInt("canSeePDATA") == 1 ? "true" : "false";
                catList.add(category);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        Logger.write("VERBOSE", "DB", "getCategories() - " + catList.size() + " returned");
        return (String[][])catList.toArray();
    }
    
    //Return the keys of each member of the category
    //if(category.equals("all")) //remember NEVER to compare strings with ==
    //    return every key you know about
    public PublicKey[] getCategoryMembers (String catID) {
        String queryStr = "";
    
        if(catID.equals("all")) { 
            queryStr = DBStrings.getAllKeys;
        } else {
            queryStr = DBStrings.getMemberKeys.replace("__CATNAME__", catID);
        }
        Vector<PublicKey> keyList = new Vector<PublicKey>();
        ResultSet keySet = query(queryStr);
        
        try {
            keySet.beforeFirst();
            while(keySet.next())
                keyList.add(Crypto.decodeKey(keySet.getString("key")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        Logger.write("VERBOSE", "DB", "getCategoryMembers(" + catID + ") - " + keyList.size() + " members");
        return (PublicKey[])keyList.toArray();

    }
    
    //In the case of no username for the key: "return Crypto.encode(k);"
    public String getName (PublicKey key) {
        String name = Crypto.encodeKey(key);
        
        try {
            name = query(DBStrings.getName.replace("__KEY__", Crypto.encodeKey(key))).beforeFirst().getString("username")
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        Logger.write("VERBOSE", "DB", "getName(...) - " + name);
        return null;
    }
    
    //"What key signed this message"
    public PublicKey getSignatory (Message m) {
        Logger.write("VERBOSE", "DB", "getSignatory(...)");
        try {
            ResultSet keys = query(DBStrings.getAllKeys);
            keys.beforeStart();
            while (keys.next())
                if (Crypto.verifySig(m, Crypto.decodeKey(keys.getString("key"))))
                    return Crypto.decodeKey(keys.getString("key"));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        Logger.write("WARNING", "DB", "getSignatory() could not find signatory");
        return null;
    }
    
    //Add to DB
    //Remember to store the signautre, create table sql may need updating
    public void addPost (Message post) {
        
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addPost(...)");
    }
    
    public boolean addKey (PublicKey k) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addKey(...)");
        return true;
    }
    
    //remember to store the signature
    //if this key has already claimed a name, forget the old one
    public void addClaim (Message claim) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addClaim(...)");
    }
    
    //remember to store the signature
    public void addRevocation (Message revocation) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addRevocation(...)");
    }
    
    //fuck the signature
    public void addPDATA (Message update) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addPDATA(...)");
    }
    
    //same as method above, but without message parameter
    public void updatePDATA (String field, String value, PublicKey k) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.updatePDATA(...)");
    }
    
    //Remember to store the signautre, create table sql may need updating
    public void addChat (Message chat) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addChat(...)");
    }
    
    //fuck the signature
    public void addMessageToChat (Message msg) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addMessageToChat(...)");
    }
    
    //Remember to store the signautre, create table sql may need updating
    /* If you can see an FPOST, it's a request to post it on your wall */
    public void addFPost (Message fpost) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addFPost(...)");
    }
    
    //Remember to store the signautre, create table sql may need updating
    public void addComment (Message comment) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addComment(...)");
    }
    
    //fuck the signature
    public void addLike (Message Like) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addLike(...)");
    }
    
    //Remember to store the signautre, create table sql may need updating
    public void addEvent (Message event) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addEvent(...)");
    }
    
    public void updatePDATApermission (String category, boolean value) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.updatePDATApermission(" + category + "," + value + ")");
    }
    
    //no duplicate names
    public boolean addCategory (String name, boolean can_see_private_details) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addCategory(" + name + "," + can_see_private_details + ")");
        return true;
    }
    
    public boolean addToCategory (String category, PublicKey key) {
        //REPLACE ME
        Logger.write("UNIMPL", "DB", "Unimplemented method Database.addToCategory(" + category + ",...)");
        return true;
    }
}
