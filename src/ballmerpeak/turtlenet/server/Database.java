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
    
    public void execute (String query) throws java.sql.SQLException {
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
            throw e;
        }
    }
    
    public ResultSet query (String query) throws java.sql.SQLException {
        if (query.indexOf('(') != -1)
            Logger.write("VERBOSE", "DB", "query(\"" + query.substring(0,query.indexOf('(')) + "...\")");
        else
            Logger.write("VERBOSE", "DB", "query(\"" + query.substring(0,20) + "...\")");
        
        try {
            Statement statement = dbConnection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet r = statement.executeQuery(query);
            return r;
        } catch (java.sql.SQLException e) {
            Logger.write("RED", "DB", "Failed to query database: " + e);
            throw e;
        }
    }
    
    //Get from DB
    public String getPDATA(String field, PublicKey key) {
        Logger.write("VERBOSE", "DB", "getPDATA(" + field + ",...)"); 
        String value = "EXCEPTION IN GETPDATA";
        try {
            String strKey = Crypto.encodeKey(key);
            String sqlStatement  = DBStrings.getPDATA.replace("__FIELD__", field);
            sqlStatement = sqlStatement.replace("__KEY__", strKey); //mods SQL template

            ResultSet results = query(sqlStatement);
            if(results.next() )
                value = results.getString(field); //gets current value in 'field'
            else
                value = "<No Value>";
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return value;
    }
    
    //Set the CMD to POST in the Message constructor
    public Message[] getWallPost (PublicKey key) {
        Logger.write("VERBOSE", "DB", "getWallPost(...)");
        Vector<Message> posts = new Vector<Message>();
        try {
            String sqlStatement  = DBStrings.getWallPostSigs.replace("__KEY__", Crypto.encodeKey(key) );
            ResultSet results = query(sqlStatement);
        
            while (results.next() ) {
                Vector<String> visibleTo = new Vector<String>();
                ResultSet currentPost = query(DBStrings.getPost.replace("__SIG__", Integer.toString(results.getInt("sig") ) ) );
                ResultSet currentPostVisibleTo = query(DBStrings.getVisibleTo.replace("__SIG__", Integer.toString(results.getInt("sig"))));
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
        
        return (Message[])posts.toArray();
    }
    
    //Return all conversations
    public Conversation[] getConversations () {
        Vector<Conversation> convoList = new Vector<Conversation>();
        Logger.write("VERBOSE", "DB", "getConversations()");

        try {
            ResultSet convoSet = query(DBStrings.getConversations);
            while (convoSet.next())
                convoList.add(getConversation(convoSet.getString("convoID")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return (Conversation[])convoList.toArray();
    }
    
    //Get keys of all people in the given conversation
    public PublicKey[] getPeopleInConvo (String sig) {
        Logger.write("VERBOSE", "DB", "getPeopleInConvo(...)");
        Vector<PublicKey> keys = new Vector<PublicKey>();
        
        try {
            ResultSet keySet = query(DBStrings.getConversationMembers.replace("__SIG__", sig));
            while (keySet.next())
                keys.add(Crypto.decodeKey(keySet.getString("key")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return (PublicKey[])keys.toArray();
    }
    
    //Reurn a conversation object
    public Conversation getConversation (String sig) {
        Logger.write("VERBOSE", "DB", "getConversation(...)");    
        try {
            ResultSet convoSet = query(DBStrings.getConversation.replace("__SIG__", sig));
            if(convoSet.next() ) {
                String timestamp = convoSet.getString("timeCreated");
                ResultSet messages = query(DBStrings.getConversationMessages.replace("__SIG__", sig));
                String firstMsg;
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
        Logger.write("VERBOSE", "DB", "getConversationMessages(...)");
        Vector<String[]> messagesList = new Vector<String[]>();
        
        try {
            ResultSet messageSet = query(DBStrings.getConversationMessages.replace("__SIG__", sig));
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

        return (String[][])messagesList.toArray();
    }
    
    //If multiple people have the same username then:
    //Logger.write("FATAL", "DB", "Duplicate usernames");
    //System.exit(1);
    public PublicKey getKey (String userName) {
        Logger.write("VERBOSE", "DB", "getKey(" + userName + ")");
        int nameCount = 0;
        String key = "<No Key>";
        
        try {
            ResultSet results = query(DBStrings.getKey.replace("__USERNAME__", userName) );
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

        return Crypto.decodeKey(key);
    }
    
    //Return the name of each member and if it can see your profile info
    //In this format: {{"friends", "false"}, {"family", "true"}, etc.}
    public String[][] getCategories () {
        Logger.write("VERBOSE", "DB", "getCategories()");
        Vector<String[]> catList = new Vector<String[]>();
        String catName;
        String canSeePDATA;
        
        try {
            ResultSet categorySet = query(DBStrings.getCategories);
            while(categorySet.next() ) {
                String[] category = new String[2];
                category[0] = categorySet.getString("catID");
                category[1] = categorySet.getInt("canSeePDATA") == 1 ? "true" : "false";
                catList.add(category);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        return (String[][])catList.toArray();
    }
    
    //Return the keys of each member of the category
    //if(category.equals("all")) //remember NEVER to compare strings with ==
    //    return every key you know about
    public PublicKey[] getCategoryMembers (String catID) {
        Logger.write("VERBOSE", "DB", "getCategoryMembers(" + catID + ")");
        String queryStr = "";
    
        if(catID.equals("all")) { 
            queryStr = DBStrings.getAllKeys;
        } else {
            queryStr = DBStrings.getMemberKeys.replace("__CATNAME__", catID);
        }
        Vector<PublicKey> keyList = new Vector<PublicKey>();
        
        try {
            ResultSet keySet = query(queryStr);
            while(keySet.next())
                keyList.add(Crypto.decodeKey(keySet.getString("key")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        return (PublicKey[])keyList.toArray();

    }
    
    //In the case of no username for the key: "return Crypto.encode(k);"
    public String getName (PublicKey key) {
        Logger.write("VERBOSE", "DB", "getName(...)");
        String name = Crypto.encodeKey(key);
        
        try {
            ResultSet nameRow = query(DBStrings.getName.replace("__KEY__", Crypto.encodeKey(key)));
            name = nameRow.getString("username");
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        return null;
    }
    
    //"What key signed this message"
    public PublicKey getSignatory (Message m) {
        Logger.write("VERBOSE", "DB", "getSignatory(...)");
        try {
            ResultSet keys = query(DBStrings.getAllKeys);
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
    public boolean addPost (Message post) {
        Logger.write("VERBOSE", "DB", "addPost(...)");
        
        try {
            execute(DBStrings.addPost.replace("__SIG__", post.getSig())
                                     .replace("__msgText__", post.POSTgetText())
                                     .replace("__time__", Long.toString(post.getTimestamp()))
                                     .replace("__recieverKey__", post.POSTgetWall())
                                     .replace("__sendersKey__", Crypto.encodeKey(getSignatory(post))));
            String[] visibleTo = post.POSTgetVisibleTo();
            for (int i = 0; i < visibleTo.length; i++)
                execute(DBStrings.addPostVisibility.replace("__postSig", post.getSig()).replace("__key__", visibleTo[i]));
            return true;
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
    }
    
    public boolean addKey (PublicKey k) {
        Logger.write("VERBOSE", "DB", "addKey(...)");
        
        try {
            execute(DBStrings.addKey.replace("__key__", Crypto.encodeKey(k)));
            return validateClaims(k);
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return false;
    }
    
    //Update k's username by validating claims
    public boolean validateClaims(PublicKey k) {
        Logger.write("VERBOSE", "DB", "validateClaims(...)");
        try {
            ResultSet claimSet = query(DBStrings.getClaims);
            while (claimSet.next()) {
                Message msg = new Message("CLAIM",
                                          claimSet.getString("name"),
                                          Long.parseLong(claimSet.getString("claimTime")),
                                          claimSet.getString("sig"));
                if (Crypto.verifySig(msg, k)) {
                    execute(DBStrings.newUsername.replace("__name__", msg.CLAIMgetName()).replace("__key__", Crypto.encodeKey(k)));
                    execute(DBStrings.removeClaim.replace("__sig__", msg.getSig()));
                    Logger.write("INFO", "DB", "Claim for " + msg.CLAIMgetName() + " verified");
                }
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        return true;
    }
    
    //if this key has already claimed a name, forget the old one
    public boolean addClaim (Message claim) {
        Logger.write("VERBOSE", "DB", "addClaim(...)");
        
        try {
            execute(DBStrings.addClaim.replace("__sig__", claim.getSig())
                                      .replace("__name__", claim.CLAIMgetName())
                                      .replace("__time__", Long.toString(claim.getTimestamp())));
            
            ResultSet everyone = query(DBStrings.getAllKeys);
            while (everyone.next())
                validateClaims(Crypto.decodeKey(everyone.getString("key")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        return true;
    }
    
    public boolean addRevocation (Message revocation) {
        Logger.write("VERBOSE", "DB", "addRevocation(...)");
        
        try {
            execute(DBStrings.addRevocation.replace("__sig__", revocation.getSig())
                                           .replace("__time__", Long.toString(revocation.REVOKEgetTime()))
                                           .replace("__creationTime__", Long.toString(revocation.getTimestamp())));
            //TODO//////////////////////////////Erase revoked content in DB///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addPDATA (Message update) {
        Logger.write("VERBOSE", "DB", "addPDATA(...)");
        boolean ret = true;
        
        String[][] updates = update.PDATAgetValues();
        for (int i = 0; i < updates.length; i++)
            if (!updatePDATA(updates[i][0], updates[i][1], getSignatory(update)))
                ret = false;
        
        return ret;
    }
    
    public boolean updatePDATA (String field, String value, PublicKey k) {
        Logger.write("VERBOSE", "DB", "updatePDATA(...)");
        
        try {
            execute(DBStrings.addPDATA.replace("__field__", field)
                                      .replace("__value__", value)
                                      .replace("__key__", Crypto.encodeKey(k)));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addConvo (Message convo) {
        Logger.write("VERBOSE", "DB", "addConvo(...)");
        
        try {
            execute(DBStrings.addConvo.replace("__sig__", convo.getSig())
                                      .replace("__time__", Long.toString(convo.getTimestamp())));
            String[] keys = convo.CHATgetKeys();
            for (int i = 0; i < keys.length; i++) {
                execute(DBStrings.addConvoParticipant.replace("__sig__", convo.getSig())
                                                     .replace("__key__", keys[i]));
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addMessageToChat (Message msg) {
        Logger.write("VERBOSE", "DB", "addMessageToChat(...)");
        
        try {
            execute(DBStrings.addMessageToConvo.replace("__convoID__", msg.PCHATgetConversationID())
                                               .replace("__sendersKey__", Crypto.encodeKey(getSignatory(msg)))
                                               .replace("__msgText__", msg.PCHATgetText())
                                               .replace("__time__", Long.toString(msg.getTimestamp())));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addComment (Message comment) {
        Logger.write("VERBOSE", "DB", "addComment(...)");
        
        try {
            execute(DBStrings.addComment.replace("__sig__", comment.getSig())
                                        .replace("__msgText__", comment.CMNTgetText())
                                        .replace("__parent__", comment.CMNTgetItemID())
                                        .replace("__commenterKey__", Crypto.encodeKey(getSignatory(comment)))
                                        .replace("__creationTime__", Long.toString(comment.getTimestamp())));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addLike (Message like) {
        Logger.write("VERBOSE", "DB", "addLike(...)");
        
        try {
            execute(DBStrings.addLike.replace("__likerKey__", Crypto.encodeKey(getSignatory(like)))
                                     .replace("__parent__", like.LIKEgetItemID()));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addEvent (Message event) {
        Logger.write("VERBOSE", "DB", "addEvent(...)");
        try {
            execute(DBStrings.addEvent.replace("__sig__", event.getSig())
                                      .replace("__startTime__", Long.toString(event.EVNTgetStart()))
                                      .replace("__endTime", Long.toString(event.EVNTgetEnd()))
                                      .replace("__creatorKey__", Crypto.encodeKey(getSignatory(event)))
                                      .replace("__accepted__", "0")
                                      .replace("__name__", event.EVNTgetName())
                                      .replace("__creationTime__", Long.toString(event.getTimestamp())));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean acceptEvent (String sig) {
        Logger.write("VERBOSE", "DB", "acceptEvent(...)");
        try {
            execute(DBStrings.acceptEvent.replace("__sig__", sig));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean declineEvent (String sig) {
        Logger.write("VERBOSE", "DB", "declineEvent(...)");
        try {
            execute(DBStrings.declineEvent.replace("__sig__", sig));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean updatePDATApermission (String category, boolean value) {
        Logger.write("VERBOSE", "DB", "updatePDATApermission(...)");
        try {
            execute(DBStrings.updatePDATApermission.replace("__catID__", category)
                                                   .replace("__bool__", value?"1":"0"));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    //no duplicate names
    public boolean addCategory (String name, boolean can_see_private_details) {
        Logger.write("VERBOSE", "DB", "addCategory(...)");
        try {
            execute(DBStrings.addCategory.replace("__catID__", name)
                                         .replace("__canSeePDATA__", can_see_private_details?"1":"0"));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    public boolean addToCategory (String category, PublicKey key) {
        Logger.write("VERBOSE", "DB", "addToCategory(...)");
        try {
            execute(DBStrings.addToCategory.replace("__catID__", category)
                                           .replace("__key__", Crypto.encodeKey(key)));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
}
