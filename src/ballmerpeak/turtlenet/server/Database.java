/*! \mainpage A note on notation
 * The following format is often used to describe arrays:
 *    {{"a", "b"}, {"c", "d"}, {"e", "f"}}
 *
 * This represents a String[3][2]
 * An array of 3 String[2]'s
 * Each String[2] contains two strings [0] and [1]
 * So [1][1] = "d"
 * and [2][0] = "e"
 *
 * This format is used to describe return formats.
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
import java.util.Arrays;

public class Database {
    public static String path = "./db"; //!<path to database directory
    private Connection dbConnection;
    private String password = "UNSET";  //!< Users password, used to encrypt local data

    /** Construct a database object.
     * Creates the database if it doesn't exist, if it does exit then it is decrypted.
     * It creates a directory (Database.path) to store local data if it doesn't
     * already exist.
     * It then connects to the database and returns.
     * \param pw The password the database is/should be encrypted with.
     */
    public Database (String pw) {
        password = pw;
        dbConnection = null;
        if (DBExists()) dbConnect(true); else dbCreate();
    }
    
    /** Test whether the directory used for storing local data exists.
     * The directory is specified by Database.path.
     * \return true is the directory exists, false otherwise.
     */
    public static boolean DBDirExists() {
        File dir = new File(path);
        return dir.exists();
    }
    
    /** Checks whether the database exists or not.
     * \return true if the database exists, false otherwise.
     */
    public static boolean DBExists() {
        File edb = new File(path + "/turtlenet.db.aes");
        File db = new File(path + "/turtlenet.db");
        return db.exists() || edb.exists();
    }
    
    /** Creates a directory to store local data.
     * The directory is that specified in Database.path.
     */
    public static boolean createDBDir() {
        return (new File(path)).mkdirs();
    }
    
    /** Creates a new database.
     * Called automatically by the consturctor.
     */
    public void dbCreate() {
        Logger.write("INFO", "DB", "Creating database");
        try {
            if (!Database.DBDirExists())
                Database.createDBDir();
            dbConnect(false);
            for (int i = 0; i < DBStrings.createDB.length; i++)
                execute(DBStrings.createDB[i]);
        } catch (Exception e) {
            Logger.write("FATAL", "DB", "Failed to create databse: " + e);
        }
    }

    /** Connects to an extant database.
     * Called automatically by the constructor.
     * \param dbexists true if a database already exists, false otherwise.
     * \return returns true if successful at connecting, false otherwise.
     */
    public boolean dbConnect(boolean dbexists) {
        if (dbexists)
            if (!Crypto.decryptDB(password))
                Logger.write("FATAL", "DB", "failed to decrypt database");
        
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

    /** Disconnects from the database.
     */
    public void dbDisconnect() {
        Logger.write("INFO", "DB", "Disconnecting from database");
        try {
            dbConnection.close();
        } catch(Exception e) { //Exception logged to disk, program allowed to continue
            Logger.write("FATAL", "DB", "Could not disconnect: " + e.getClass().getName() + ": " + e.getMessage() );
        }
        
        if (!Crypto.encryptDB(password))
            Logger.write("FATAL", "DB", "failed to encrypt database");
    }
    
    /** Executes the given SQL query.
     * \warning Throws a java.sql.SQLException on failure.
     * \param query The query to execute.
     */
    public void execute (String query) throws java.sql.SQLException {
        try {
            /*
            if (query.indexOf('(') != -1)
                Logger.write("VERBOSE", "DB", "execute(\"" + query.substring(0,query.indexOf('(')) + "...\")");
            else
                Logger.write("VERBOSE", "DB", "execute(\"" + query.substring(0,20) + "...\")");
            */
            Logger.write("VERBOSE", "DB", "execute(\"" + query + "\")");
        
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
    
    /** Executes the given SQL query, returns the results.
     * \warning Throws a java.sql.SQLException on failure.
     * \param query The query to execute.
     * \return A ResultSet, the rows returned by the query.
     */
    public ResultSet query (String query) throws java.sql.SQLException {
        /*
        if (query.indexOf('(') != -1)
            Logger.write("VERBOSE", "DB", "query(\"" + query.substring(0,query.indexOf('(')) + "...\")");
        else
            Logger.write("VERBOSE", "DB", "query(\"" + query.substring(0,20) + "...\")");
        */
        Logger.write("VERBOSE", "DB", "query(\"" + query + "\")");
        
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
    
    /** Gets the specified piece of profile data for the specified user.
     * \param field The name of the field the value of which you wish to retrieve.
     *              valid options are: email, name, gender, and birthday.
     * \param key The key of the user which you wish to retrieve data about.
     * \return The value of the specified field for the specified user. Returns
     *         "<no value>" if no value is known.
     */
    public String getPDATA(String field, PublicKey key) {
        Logger.write("VERBOSE", "DB", "getPDATA(" + field + ",...)"); 
        String value = "";
        try {
            String strKey = Crypto.encodeKey(key);
            String sqlStatement  = DBStrings.getPDATA.replace("__FIELD__", field);
            sqlStatement = sqlStatement.replace("__KEY__", strKey); //mods SQL template

            ResultSet results = query(sqlStatement);
            if(results.next())
                value = results.getString(field); //gets current value in 'field'
            else
                value = "<No Value>";
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        if (value != null)
            return value;
        else
            return "<no value>";
    }
    
    /** Get the posts on the wall of the given user.
     * \param key The public key of the user the wall of whom you are interested in.
     * \return An array of Messages, ordered chronologically so that element 0
     *         is the oldest post on their wall.
     */
    public Message[] getWallPost (PublicKey key) {
        Logger.write("VERBOSE", "DB", "getWallPost(...)");
        Vector<Message> posts = new Vector<Message>();
        try {
            String sqlStatement = DBStrings.getWallPostSigs.replace("__KEY__", Crypto.encodeKey(key) );
            ResultSet results = query(sqlStatement);
        
            while (results.next()) {
                Vector<String> visibleTo = new Vector<String>();
                ResultSet currentPost = query(DBStrings.getPost.replace("__SIG__", results.getString("sig")));
                ResultSet currentPostVisibleTo = query(DBStrings.getVisibleTo.replace("__SIG__", results.getString("sig")));
                while(currentPostVisibleTo.next())
                    visibleTo.add(currentPostVisibleTo.getString("key") );

                if(currentPost.next()) {
                    Message m = new MessageFactory().newPOST(currentPost.getString("msgText"), currentPost.getString("recieverKey"), (visibleTo.toArray(new String[0])) );
                    m.timestamp = Long.parseLong(currentPost.getString("time"));
                    m.signature = currentPost.getString("sig");
                    m.command = "POST";
                    posts.add(m);
                }
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return posts.toArray(new Message[0]);
    }
    
    /** Get the key of the user who created a given post.
     * \param sig The signature of the post.
     * \return The key of the user who created the post.
     * Returns "<POST DOESN'T EXIST>" if the post doesn't exist, may also return
     * "ERROR" if there is an SQL error.
     */
    public String getWallPostSender (String sig) {
        Logger.write("VERBOSE", "DB", "getWallPostSender(...)");
        try {
            ResultSet sendersKey = query(DBStrings.getPostSender.replace("__SIG__", sig));
            if (sendersKey.next())
                return sendersKey.getString("sendersKey");
            else
                return "<POST DOESN'T EXIST>";
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return "ERROR";
        }
    }
    
    /** Get all comments on a given post or comment.
     * \param sig The signature of the item whose comments one desires.
     * \return An array of Messages, ordered chronologically so that element 0
     *         is the oldest comment on the item.
     */
    public Message[] getComments (String sig) {
        Vector<Message> comments = new Vector<Message>();
        Logger.write("VERBOSE", "DB", "getComments(...)");
    
        try {
            ResultSet commentSet = query(DBStrings.getComments.replace("__PARENT__", sig));
            while (commentSet.next()) {
                Message cmnt = new MessageFactory().newCMNT(sig, commentSet.getString("msgText"));
                cmnt.timestamp = Long.parseLong(commentSet.getString("creationTime"));
                cmnt.signature = commentSet.getString("sig");
                comments.add(cmnt);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return comments.toArray(new Message[0]); 
    }
    
    /** Get the time that a given users wall was last posted on.
     * \param key The public key of the user the wall of whom you are interested in.
     * \return The number of milliseconds from midnight january first 1970 to
     *  the time of the most recent post placed on the specified users wall.
     */
    public Long timeMostRecentWallPost (PublicKey key) {
        Logger.write("VERBOSE", "DB", "timeMostRecentWallPost(...)");
        try {
            ResultSet mostRecent = query(DBStrings.mostRecentWallPost.replace("__KEY__", Crypto.encodeKey(key)));
            if (mostRecent.next())
                return Long.parseLong(mostRecent.getString("maxtime"));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        return 0L;
    }
    
    /** Query whether or not a given comment or post is liked.
     * \param sig The signature of the comment or post being examined.
     * \return true if the item is liked, false otherwise.
     */
    public boolean isLiked (String sig) {
        Logger.write("VERBOSE", "DB", "isLiked(...)");
        int ret = 0;

        try {
            ResultSet row = query(DBStrings.getLike.replace("__SIG__", sig));
            return row.next();
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return false;
    }
    
    /** Get all conversations you know about.
     * \return An array of all conversations you know about in no particular order.
     */
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
        
        return convoList.toArray(new Conversation[0]);
    }
    
    /** Get the keys of users involved in a given conversation.
     * \param sig The signature of the conversation being examined.
     * \return An array of PublicKeys containing the keys of every user who is
     * in the specified conversation.
     */
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
        
        return keys.toArray(new PublicKey[0]);
    }
    
    /** Get details of a specific conversation, but not the messages therein.
     * \param sig The signature of the conversation you want details about.
     * \return A conversation object with the details of the specified conversation.
     */
    public Conversation getConversation (String sig) {
        Logger.write("VERBOSE", "DB", "getConversation(...)");    
        try {
            ResultSet convoSet = query(DBStrings.getConversation.replace("__SIG__", sig));
            if(convoSet.next()) {
                String timestamp = convoSet.getString("time");
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
                Logger.write("WARNING", "DB", "getConversation(...) empty conversation: " + sig);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        return new Conversation();
    }
    
    /** Get all the messages from a given conversation.
     * \param sig The signature of the conversation you want the messages of.
     * \return The messages in the specified conversation, in chronological
     *  order (i.e.: Element 0 is the oldest message in the conversation).
     * Data is in this format: {{username, time, msg}, {username, time, msg}, etc.}
     */
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

        return messagesList.toArray(new String[0][0]);
    }
    
    /** Retrieve the key of the specified user.
     * \param userName The username of the user which you wish to know the key of.
     * \return The key of the specified user. Returns "--INVALID KEYSTRING--" if
     *         no key is known.
     */
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
    
    /** Query whether or not a given category can see your profile information.
     * \param category The name of the category in question.
     * \return true is the people in the category can see your profile information,
     * false otherwise.
     */
    public boolean canSeePDATA (String category) {
        Logger.write("VERBOSE", "DB", "canSeePDATA()");
        
        try {
            ResultSet categorySet = query(DBStrings.canSeePDATA.replace("__CATID__", category));
            if (categorySet.next()) {
                return categorySet.getInt("canSeePDATA") == 1 ? true : false;
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return false;
    }
    
    /** Get a list of all categories.
     * \return The names of each category and if it can see your profile info.
     * Data is in this format: {{"friends", "false"}, {"family", "true"}, etc.}
     */
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
        
        Logger.write("VERBOSE", "DB", "getCategories() returning " + catList.toArray().length + " categories");
        return catList.toArray(new String[0][0]);
    }
    
    /** Get all members of a given category.
     * If the category given is "all" then all known people are returned.
     * \param catID The name of the category of which you want to know the members.
     * \return An array of PublicKeys containing the key of every user in the
     *  specified category.
     */
    public PublicKey[] getCategoryMembers (String catID) {
        Logger.write("VERBOSE", "DB", "getCategoryMembers(" + catID + ")");
        String queryStr = "";
    
        if(catID.toLowerCase().equals("all"))
            queryStr = DBStrings.getAllKeys;
        else
            queryStr = DBStrings.getMemberKeys.replace("__CATNAME__", catID);
        
        Vector<PublicKey> keyList = new Vector<PublicKey>();
        
        try {
            ResultSet keySet = query(queryStr);
            while(keySet.next()) {
                if(catID.toLowerCase().equals("all"))
                    keyList.add(Crypto.decodeKey(keySet.getString("key")));
                else
                    keyList.add(Crypto.decodeKey(keySet.getString("userKey")));
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        Logger.write("VERBOSE", "DB", "getCategoryMembers(" + catID + ") returning " + keyList.toArray().length + " members");
        return keyList.toArray(new PublicKey[0]);
    }
    
    /** Get all people who can see a given post or comment.
     * The visibility of comments is the same as that of their parents.
     * \param sig The signature of the post or comment being considered.
     * \return An array of PublicKeys which contains the key of every user which
     *  is able to decrypt and view the given comment or post.
     */
    public PublicKey[] getVisibilityOfParent(String sig) {
        Logger.write("VERBOSE", "DB", "getVisibilityOfParent(" + sig + ")");
        
        try {
            ResultSet postWithSig = query(DBStrings.getPost.replace("__SIG__", sig));
            if (postWithSig.next()) { //sig is a post
                Logger.write("VERBOSE", "DB", "parent is a wall post: " + sig);
                return getPostVisibleTo(sig);
            } else { //sig is a comment
                ResultSet commentWithSig = query(DBStrings.getComment.replace("__SIG__", sig));
                if (commentWithSig.next())
                    return getVisibilityOfParent(commentWithSig.getString("parent"));
                else
                    Logger.write("ERROR", "DB", "getVisibilityOfParent has no root");
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return null;
    }
    
    /** Get all people who can see a given post.
     * \param sig The signature of the post being considered.
     * \return An array of PublicKeys which contains the key of every user which
     *  is able to decrypt and view the given post.
     */
    public PublicKey[] getPostVisibleTo (String sig) {
        Logger.write("VERBOSE", "DB", "getVisibleTo(...)");
        Vector<PublicKey> keyList = new Vector<PublicKey>();
        
        try {
            ResultSet keyRows = query(DBStrings.getVisibleTo.replace("__SIG__", sig));
            while(keyRows.next())
                keyList.add(Crypto.decodeKey(keyRows.getString("key")));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return keyList.toArray(new PublicKey[0]);
    }
    
    /** Retreives the most recent username associated with a given key.
     * \param key The public key of the user whose username you wish to know.
     * \return The most recent username of the user with the given public key.
     * "<no username>" is returned if no username is known.
     */
    public String getName (PublicKey key) {
        Logger.write("VERBOSE", "DB", "getName(...)");
        String name = "";
        
        try {
            ResultSet nameRow = query(DBStrings.getName.replace("__KEY__", Crypto.encodeKey(key)));
            if (nameRow.next())
                name = nameRow.getString("username");
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }

        if (name != null)
            return name;
        else
            return "<no username>";
    }
    
    /** Discover who sent a given message.
     * Derived from the signautre on the message and known public keys.
     * \param m The message whose author you wish to determine.
     * \return The public key of the user who wrote m.
     * null is returned if no signatory can be found.
     */
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
    
    /** Add a post.
     * \param post The Message object representing the post.
     * \return "true" if successful, "false" otherwise.
     */
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
                execute(DBStrings.addPostVisibility.replace("__postSig__", post.getSig()).replace("__key__", visibleTo[i]));
            return true;
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
    }
    
    /** Add another users public key.
     * \param msg The Message object representing the ADDKEY message.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addKey (Message msg) {
        return addKey(Crypto.decodeKey(msg.ADDKEYgetKey()));
    }
    
    /** Add another users public key.
     * \param k The PublicKey to add.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addKey (PublicKey k) {
        Logger.write("VERBOSE", "DB", "addKey(...)");
        
        try {
            execute(DBStrings.addKey.replace("__key__", Crypto.encodeKey(k)));
            boolean ret = validateClaims(k);
            if (!calcRevocationKeys(k))
                ret = false;
            return ret;
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return false;
    }
    
    /** Check username claims against a given public key.
     * Called automatically after a new key is added to ensure that old username
     *  claims are recognised.
     * \param k The key to be considered.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean validateClaims(PublicKey k) {
        if (k == null) {
            Logger.write("ERROR", "DB", "validateClaims(...) called with null key");
            return false;
        }
        
        Logger.write("VERBOSE", "DB", "validateClaims(...)");
        
        try {
            ResultSet claimSet = query(DBStrings.getClaims);
            while (claimSet.next()) {
                Message msg = new Message("CLAIM",
                                          claimSet.getString("name"),
                                          Long.parseLong(claimSet.getString("claimTime")),
                                          claimSet.getString("sig"));
                
                Logger.write("VERBOSE", "DB", "Considering Claim for name: \"" + claimSet.getString("name") + "\"");
                Logger.write("VERBOSE", "DB", "                      time: \"" + Long.toString(Long.parseLong(claimSet.getString("claimTime"))) + "\"");
                Logger.write("VERBOSE", "DB", "                       sig: \"" + claimSet.getString("sig") + "\"");
                
                PublicKey signatory = getSignatory(msg);
                if (signatory != null && signatory.equals(k)) {
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
    
    /** Update the keys column in the key revocations table.
     * Called automatically after a new key is added to ensure it isn't revoked.
     * \param k The new key.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean calcRevocationKeys (PublicKey k) {
        if (k == null) {
            Logger.write("ERROR", "DB", "calcRevocationKeys(...) called with null key");
            return false;
        }
        
        Logger.write("VERBOSE", "DB", "calcRevocationKeys(...)");
        
        try {
            ResultSet revocationSet = query(DBStrings.getRevocations);
            while (revocationSet.next()) {
                Message msg = new Message("REVOKE",
                                          revocationSet.getString("timeOfLeak"),
                                          Long.parseLong(revocationSet.getString("creationTime")),
                                          revocationSet.getString("sig")); 
                PublicKey signer = getSignatory(msg);
                if (signer != null && signer.equals(k)) {
                    execute(DBStrings.updateRevocationKey.replace("__KEY__", Crypto.encodeKey(k))
                                                         .replace("__SIG__", revocationSet.getString("sig")));
                }
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        return true;
    }
    
    /** Add a claim.
     * If a username is already claimed, forget the old name.
     * \param claim The Message object representing the claim.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addClaim (Message claim) {
        Logger.write("VERBOSE", "DB", "addClaim("+ claim.CLAIMgetName() +")");
        
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
    
    /** Add a revocation.
     * \param revocation The Message object representing the revocation.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addRevocation (Message revocation) {
        Logger.write("VERBOSE", "DB", "-------addRevocation(...)-------");
        
        try {
            execute(DBStrings.addRevocation.replace("__key__", Crypto.encodeKey(getSignatory(revocation)))
                                           .replace("__sig__", revocation.getSig())
                                           .replace("__time__", Long.toString(revocation.REVOKEgetTime()))
                                           .replace("__creationTime__", Long.toString(revocation.getTimestamp())));
            return eraseContentFrom(getSignatory(revocation));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
    }
    
    /** Check if a given key is revoked.
     * \param key The key being checked.
     * \return "true" if revoked, "false" otherwise.
     */
    public boolean isRevoked (PublicKey key) {
        Logger.write("VERBOSE", "DB", "isRevoked(...)");
        
        try {
            return query(DBStrings.isRevoked.replace("__KEY__", Crypto.encodeKey(key))).next();
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
    }
    
    /** Erase all content signed by a given key.
     * \param key The key whose data is being expurgated.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean eraseContentFrom(PublicKey key) {
        Logger.write("VERBOSE", "DB", "-------eraseContentFrom(...)-------");
        String keyStr = Crypto.encodeKey(key);
        
        try {
            execute(DBStrings.removeMessageAccess.replace("__KEY__", keyStr));
            execute(DBStrings.removeMessages.replace("__KEY__", keyStr));
            execute(DBStrings.removePosts.replace("__KEY__", keyStr));
            execute(DBStrings.removePostVisibility.replace("__KEY__", keyStr));
            execute(DBStrings.removeUser.replace("__KEY__", keyStr));
            execute(DBStrings.removeFromCategories.replace("__KEY__", keyStr));
            execute(DBStrings.removeLikes.replace("__KEY__", keyStr));
            execute(DBStrings.removeComments.replace("__KEY__", keyStr));
            execute(DBStrings.removeEvents.replace("__KEY__", keyStr));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    /** Add new profile information.
     * \param update The Message object representing the update.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addPDATA (Message update) {
        Logger.write("VERBOSE", "DB", "addPDATA(...)");
        boolean ret = true;
        
        String[][] updates = update.PDATAgetValues();
        for (int i = 0; i < updates.length; i++)
            if (!updatePDATA(updates[i][0], updates[i][1], getSignatory(update)))
                ret = false;
        
        return ret;
    }
    
    /** Update a given users profile information.
     * \param field The name of the field the value of which you wish to update.
     *              valid options are: email, name, gender, and birthday.
     * \param value The desired value.
     * \param k The key of the user whos profile information is being updated.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean updatePDATA (String field, String value, PublicKey k) {
        Logger.write("VERBOSE", "DB", "updatePDATA(" + field + ", " + value + ", ...)");
        
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
    
    /** Add a new conversation.
     * \param convo The Message object representing the conversation.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Add a message to an extant conversation.
     * \param msg The Message object representing the new message to be added.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addMessageToChat (Message msg) {
        Logger.write("VERBOSE", "DB", "addMessageToChat(...)");
        
        try {
            boolean duplicate = false;
            
            String[][] messagesInConvo = getConversationMessages(msg.PCHATgetConversationID());
            for (int i = 0; i < messagesInConvo.length; i++)
                if (messagesInConvo[i][1].equals(Long.toString(msg.getTimestamp())) && messagesInConvo[i][2].equals(msg.PCHATgetText()))
                    duplicate = true;
            
            if (!duplicate) {
                execute(DBStrings.addMessageToConvo.replace("__convoID__", msg.PCHATgetConversationID())
                                                   .replace("__sendersKey__", Crypto.encodeKey(getSignatory(msg)))
                                                   .replace("__msgText__", msg.PCHATgetText())
                                                   .replace("__time__", Long.toString(msg.getTimestamp())));
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    /** Add a comment to a post or comment.
     * \param comment The Message object representing the comment.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addComment (Message comment) {
        Logger.write("VERBOSE", "DB", "addComment(...)");
        
        try {
            execute(DBStrings.addComment.replace("__sig__", comment.getSig())
                                        .replace("__msgText__", comment.CMNTgetText())
                                        .replace("__parent__", comment.CMNTgetItemID())
                                        .replace("__commenterKey__", Crypto.encodeKey(getSignatory(comment)))
                                        .replace("__senderKey__", Crypto.encodeKey(getSignatory(comment)))
                                        .replace("__creationTime__", Long.toString(comment.getTimestamp())));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    /** Add a like.
     * \param like The Message object representing the like.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Add a new event.
     * \param event The Message object representing the event.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Accept an event.
     * \param sig The signature of the event you are accepting.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Decline an event.
     * \param sig The signature of the event you are declining.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Update whether or not a given category can see your profile information.
     * \param msg The Message object representing the update.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean updatePDATApermission (Message msg) {
        return updatePDATApermission(msg.UPDATECATgetName(), msg.UPDATECATgetValue());
    }
    
    /** Change whether or not a category can see your profile information.
     * \param category The name of the category you wish to update.
     * \param value The desired value, true to allow the category to see your
     *  profile data, false to forbid it.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Get a list of people who can see your profile information.
     * \return An array of PublicKeys containing every key which is able to see
     *  your profile information.
     */
    public PublicKey[] keysCanSeePDATA () {
        Logger.write("VERBOSE", "DB", "keysCanSeePDATA()");
        Vector<PublicKey> keys = new Vector<PublicKey>();
        
        try {
            ResultSet categories = query(DBStrings.categoriesCanSeePDATA);
            while (categories.next()) {
                String catname = categories.getString("catID");
                PublicKey[] memberKeys = getCategoryMembers(catname);
                for (int i = 0; i < memberKeys.length; i++)
                    if (!keys.contains(memberKeys[i]))
                        keys.add(memberKeys[i]);
            }
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
        }
        
        return keys.toArray(new PublicKey[0]);
    }
    
    /** Add a new category.
     * \param msg The Message object representing the new category.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addCategory (Message msg) {
        return addCategory(msg.ADDCATgetName(), msg.ADDCATgetValue());
    }
    
    /** Add a new category.
     * \param name The name of the new category.
     * \param can_see_private_details true if the new category ought to be able
     *  to see your profile information, false otherfile.
     * \return "true" if successful, "false" otherwise.
     */
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
    
    /** Add a user to a category.
     * \param msg The Message object representing the addition of a user to a category.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addToCategory (Message msg) {
        return addToCategory(msg.ADDTOCATgetName(), Crypto.decodeKey(msg.ADDTOCATgetKey()));
    }
    
    /** Add a user to a category.
     * \param category The category to add a user to.
     * \param key The key of the user who is being added to the specified category.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean addToCategory (String category, PublicKey key) {
        Logger.write("VERBOSE", "DB", "addToCategory(" + category + ", ...)");
        
        PublicKey[] members = getCategoryMembers(category);
        if (Arrays.asList(members).contains(key)) {
            return false;
        }
        
        try {
            execute(DBStrings.addToCategory.replace("__catID__", category)
                                           .replace("__key__", Crypto.encodeKey(key)));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    /** Remove a user from a category.
     * \param msg The Message object representing the removal of a user from a category.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean removeFromCategory (Message msg) {
        return removeFromCategory(msg.REMFROMCATgetCategory(), Crypto.decodeKey(msg.REMFROMCATgetKey()));
    }
    
    /** Remove a user form a category.
     * \param category The name of the category the user is being removed from.
     * \param key The key of the user to be removed from the category.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean removeFromCategory (String category, PublicKey key) {
        Logger.write("VERBOSE", "DB", "removeFromCategory(" + category + ", ...)");
        try {
            execute(DBStrings.removeFromCategory.replace("__catID__", category)
                                                .replace("__key__", Crypto.encodeKey(key)));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    /** Like a given post or comment.
     * \param sig The signature of the comment or post to like.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean like (String sig) {
        Logger.write("VERBOSE", "DB", "like(...)");
        try {
            execute(DBStrings.addLike.replace("__parent__", sig)
                                     .replace("__likerKey__", Crypto.encodeKey(Crypto.getPublicKey())));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
    
    /** Unlike a given post or comment.
     * \param sig The signature of the comment or post to like.
     * \return "true" if successful, "false" otherwise.
     */
    public boolean unlike (String sig) {
        Logger.write("VERBOSE", "DB", "like(...)");
        try {
            execute(DBStrings.removeLike.replace("__parent__", sig)
                                     .replace("__likerKey__", Crypto.encodeKey(Crypto.getPublicKey())));
        } catch (java.sql.SQLException e) {
            Logger.write("ERROR", "DB", "SQLException: " + e);
            return false;
        }
        
        return true;
    }
}
