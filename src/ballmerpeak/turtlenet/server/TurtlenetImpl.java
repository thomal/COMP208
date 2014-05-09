package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.*;
import java.security.*;
import ballmerpeak.turtlenet.server.TNClient;
import ballmerpeak.turtlenet.server.MessageFactory;
import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.shared.Conversation;
import ballmerpeak.turtlenet.shared.PostDetails;
import ballmerpeak.turtlenet.shared.CommentDetails;

@SuppressWarnings("serial")
public class TurtlenetImpl extends RemoteServiceServlet implements Turtlenet {
    TNClient c = null; //!< Turtlenet client that runs in the background on the backend.
    
    /** Starts Turtlenet client on the backend..
     * \return "true" if successful, "false" otherwise.
     */
    public String startTN(String password) {
        Logger.init("LOG_turtlenet");
        Logger.write("INFO", "TNImpl","startTN(" + password + ")");
        c = new TNClient(password);
        if (c != null) {
            Thread t = new Thread(c);
            t.start();
            return "success";
        } else {
            return "failure";
        }
    }
    
    /** Stops Turtlenet client on the backend..
     * \return "true" if successful, "false" otherwise.
     */
    public String stopTN() {
        Logger.write("INFO", "TNImpl","stopTN()");
        c.running = false;
        return "success";
    }
    
    /** Check if this is the first time Turtlenet has been run.
     * \return "true" if this is the first time turtlenet has been run, "false" otherwise.
     */
    public String isFirstTime() {
        return !Database.DBExists() ? "true" : "false"; //GWT can only return objects
    }
    
    /** Register on the network.
     * \param username The desired username.
     * \param password The desired password (used for local encryption, not remote).
     * \return "true" if successful, "false" otherwise. Usernames must be unique
     *  and the server enforces this.
     */
    public String register(String username, String password) {
        Logger.init("LOG_turtlenet");
        Logger.write("INFO", "TnImpl", "Registering \"" + username + "\" with PW \"" + password + "\"");
        
        if (startTN(password).equals("success")) {
            while(!c.dbReady) {
                try{
                    Logger.write("CRAP", "TnImpl", "WAITING FOR DB");
                    Thread.sleep(1000);//TODO THIS IS AWFUL PRACTICE
                }catch(Exception e){}
            }
            
            Logger.write("INFO", "TnImpl", "Started TN...continuing registration");
            if (claimUsername(username).equals("success")) {
                addKey(Crypto.encodeKey(Crypto.getPublicKey()));
                return "success";
            } else {
                Logger.write("INFO", "TnImpl", "Username taken");
                Logger.write("INFO", "TnImpl", "---REGISTRATION FAIL#tUN---");
                return "taken";
            }
        } else {
            Logger.write("ERROR", "TnImpl", "Could not start Turtlenet");
            Logger.write("ERROR", "TnImpl", "---REGISTRATION FAIL#noTN---");
            return "failure";
        }
    }
    
    /** Retreives the users username from the database.
     * \return the users username.
     */
    public String getMyUsername() {
        Logger.write("VERBOSE", "TnImpl", "getMyUsername()");
        return c.db.getName(Crypto.getPublicKey());
    }
    
    /** Retreives the most recent username associated with a given key.
     * \param key The public key of the user whose username you wish to know.
     * \return The most recent username of the user with the given public key.
     * "<no username>" is returned if no username is known.
     */
    public String getUsername(String key) {
        Logger.write("VERBOSE", "TnImpl", "getUsername(" + key + ")");
        String name = c.db.getName(Crypto.decodeKey(key));
        Logger.write("VERBOSE", "TNImpl","getUsername returning \"" + name + "\"");
        return name;
    }
    
    /** Gets the specified piece of profile data for the current user.
     * \param field The name of the field the value of which you wish to retrieve.
     *              valid options are: email, name, gender, and birthday.
     * \return The value of the specified field for the current user. Returns
     *         "<no value>" if no value is known.
     */
    public String getMyPDATA(String field) {
        Logger.write("VERBOSE", "TnImpl", "getMyPDATA(" + field + ")");
        return getPDATA(field, Crypto.encodeKey(Crypto.getPublicKey()));
    }
    
    /** Gets the specified piece of profile data for the specified user.
     * \param field The name of the field the value of which you wish to retrieve.
     *              valid options are: email, name, gender, and birthday.
     * \param key The key of the user which you wish to retrieve data about.
     * \return The value of the specified field for the specified user. Returns
     *         "<no value>" if no value is known.
     */
    public String getPDATA(String field, String key) {
        Logger.write("VERBOSE", "TnImpl", "getPDATA("+ field + ", ...)");
        return c.db.getPDATA(field, Crypto.decodeKey(key));
    }
    
    /** Retrieve the key of the current user.
     * \return The key of the current user.
     */
    public String getMyKey() {
        Logger.write("VERBOSE", "TnImpl", "getMyKey()");
        return Crypto.encodeKey(Crypto.getPublicKey());
    }
    
    /** Retrieve the key of the specified user.
     * \param username The username of the user which you wish to know the key of.
     * \return The key of the specified user. Returns "--INVALID KEYSTRING--" if
     *         no key is known.
     */
    public String getKey(String username) {
        Logger.write("VERBOSE", "TnImpl", "getKey(" + username + ")");
        return Crypto.encodeKey(c.db.getKey(username));
    }
    
    /** Get a list of all categories.
     * \return The names of each category and if it can see your profile info.
     * Data is in this format: {{"friends", "false"}, {"family", "true"}, etc.}
     */
    public String[][] getCategories () {
        Logger.write("VERBOSE", "TnImpl", "getCategories()");
        return c.db.getCategories();
    }
    
    /** Get all people you know about.
     * \return The usernames and public keys of everyone you know about.
     * Data is in this format: {{"bob", "bobs_key"}, {"john", "johns_key"}, etc.}
     */
    public String[][] getPeople () {
        Logger.write("VERBOSE", "TnImpl", "getPeople()");
        return getCategoryMembers("all");
    }
    
    /** Get all conversations you know about.
     * \return An array of all conversations you know about in no particular order.
     */
    public Conversation[] getConversations () {
        Logger.write("VERBOSE", "TnImpl", "START------------getConversations()");
        Conversation[] conversations = c.db.getConversations();
        for (int i = 0; i < conversations.length; i++) {
            Logger.write("VERBOSE", "TnImpl", "\tSig: " + conversations[i].signature);
            Logger.write("VERBOSE", "TnImpl", "\tTime: " + conversations[i].timestamp);
            Logger.write("VERBOSE", "TnImpl", "\tFirst Message: " + conversations[i].firstMessage);
            Logger.write("VERBOSE", "TnImpl", "\tUsers: " + conversations[i].users.length);
            Logger.write("VERBOSE", "TnImpl", "\tKeys: " + conversations[i].keys.length);
        }
        Logger.write("VERBOSE", "TnImpl", "END  ------------getConversations()");
        return conversations;
    }
    
    /** Get details of a specific conversation, but not the messages therein.
     * \param sig The signature of the conversation you want details about.
     * \return A conversation object with the details of the specified conversation.
     */
    public Conversation getConversation (String sig) {
        Logger.write("VERBOSE", "TnImpl", "getConversation(...)");
        return c.db.getConversation(sig);
    }
    
    /** Get all the messages from a given conversation.
     * \param sig The signature of the conversation you want the messages of.
     * \return The messages in the specified conversation, in chronological
     *  order (i.e.: Element 0 is the oldest message in the conversation).
     * Data is in this format: {{username, time, msg}, {username, time, msg}, etc.}
     */
    public String[][] getConversationMessages (String sig) {
        Logger.write("VERBOSE", "TnImpl", "getConversationMessages(...)");
        return c.db.getConversationMessages(sig);
    }
    
    /** Get all members of a given category.
     * If the category given is "all" then all known people are returned.
     * \param category The name of the category of which you want to know the members.
     * \return The username and key of each member of the specified category.
     * Data is in this format: {{"bob", "bobs_key"}, {"john", "johns_key"}, etc.}
     */
    public String[][] getCategoryMembers (String category) {
        Logger.write("VERBOSE", "TnImpl", "getCategoryMembers(" + category + ")");
        PublicKey[] keys = c.db.getCategoryMembers(category);        
        String[][] pairs = new String[keys.length][2];
        
        for (int i = 0; i < keys.length; i++) {
            pairs[i][0] = c.db.getName(keys[i]);
            pairs[i][1] = Crypto.encodeKey(keys[i]);
        }
        
        return pairs;
    }
    
    /** Get the posts on the wall of the given user.
     * \param key The public key of the user the wall of whom you are interested in.
     * \return An array of PostDetails, ordered chronologically so that element 0
     *         is the oldest post on their wall.
     */
    public PostDetails[] getWallPosts (String key) {
        Logger.write("VERBOSE", "TnImpl", "getWallPosts(...) ENTERING");
        Message[] msgs = c.db.getWallPost(Crypto.decodeKey(key));
        PostDetails[] posts = new PostDetails[msgs.length];
        for (int i = 0; i < msgs.length; i++) {
            String sig = msgs[i].getSig();
            boolean liked = c.db.isLiked(sig);
            int commentCount = c.db.getComments(sig).length;
            Long time = msgs[i].getTimestamp();
            String username = c.db.getName(Crypto.decodeKey(c.db.getWallPostSender(msgs[i].getSig())));
            String text = msgs[i].POSTgetText();
            
            posts[i] = new PostDetails(sig, liked, commentCount, time, username, text, Crypto.encodeKey(c.db.getSignatory(msgs[i])));
        }
        Logger.write("VERBOSE", "TnImpl", "getWallPosts(...) RETURNING");
        return posts;
    }
    
    /** Get the details of all comments on a given post or comment.
     * \param parent The signature of the item whose comments one desires.
     * \return An array of CommentDetails, ordered chronologically so that element 0
     *         is the oldest comment on the item.
     */
    public CommentDetails[] getComments (String parent) {
        Logger.write("VERBOSE", "TnImpl", "START----------getComments(...)");
        Message[] commentMsgs = c.db.getComments(parent);
        CommentDetails[] details = new CommentDetails[commentMsgs.length];
        
        for (int i = 0; i < commentMsgs.length; i++) {
            CommentDetails thisCmnt = new CommentDetails();
            thisCmnt.posterKey = Crypto.encodeKey(c.db.getSignatory(commentMsgs[i]));
            thisCmnt.posterName = c.db.getName(Crypto.decodeKey(thisCmnt.posterKey));
            thisCmnt.sig = commentMsgs[i].getSig();
            thisCmnt.text = commentMsgs[i].CMNTgetText();
            thisCmnt.liked = c.db.isLiked(thisCmnt.sig);
            details[i] = thisCmnt;
        }
        for (int i = 0; i < details.length; i++) {
            Logger.write("VERBOSE", "TnImpl", "comment   sig: " + details[i].sig);
            Logger.write("VERBOSE", "TnImpl", "comment  text: " + details[i].text);
            Logger.write("VERBOSE", "TnImpl", "comment liked: " + details[i].liked);
        }
        
        Logger.write("VERBOSE", "TnImpl", "END -----------getComments(...)");
        return details;
    }
    
    /** Get the time that a given users wall was last posted on.
     * \param key The public key of the user the wall of whom you are interested in.
     * \return The number of milliseconds from midnight january first 1970 to
     *  the time of the most recent post placed on the specified users wall.
     */
    public Long timeMostRecentWallPost (String key) {
        return c.db.timeMostRecentWallPost(Crypto.decodeKey(key));
    }
    
    /** Get the time that a given conversation was last posted in.
     * \param sig The signature of the conversation being examined.
     * \return The number of milliseconds from midnight january first 1970 to
     *  the time of the most recent message in the specified conversation.
     */
    public Long getConvoLastUpdated (String sig) {
        String[][] details = c.db.getConversationMessages(sig);
        if (details.length > 0)
            return Long.parseLong(details[details.length-1][1]);
        else
            return 0L;
    }
    
    /** Get the time that a given comment or post was last commented on.
     * \param sig The signature of the comment or post being examined.
     * \return The number of milliseconds from midnight january first 1970 to
     *  the time of the most recent comment was posted on the given comment or
     *  post.
     */
    public Long getPostLastCommented (String sig) {
        Message[] comments = c.db.getComments(sig);
        return comments[comments.length-1].getTimestamp();
    }
    
    /** Register a new username on the network.
     * \param uname The desired username.
     * \return "true" if successful, "false" otherwise. Usernames must be unique
     *  and the server enforces this.
     */
    public String claimUsername (String uname) {
        Logger.write("VERBOSE", "TnImpl", "claimUsername(" + uname + ")");
        c.db.addClaim(new MessageFactory().newCLAIM(uname));
        if(c.connection.claimName(uname))
            return "success";
        else
            return "failure";
    }
    
    /** Change your profile information.
     * \param field The name of the field the value of which you wish to update.
     *              valid options are: email, name, gender, and birthday.
     * \param value The desired value.
     * \return "true" if successful, "false" otherwise.
     */
    public String updatePDATA (String field, String value) {
        String ret = "success";
        Logger.write("VERBOSE", "TnImpl", "updatePDATA(" + field + ", " + value + ")");
        PublicKey[] keys = c.db.keysCanSeePDATA();
        Message message = new MessageFactory().newPDATA(field, value);
        for (int i = 0; i < keys.length; i++)
            if (!c.connection.postMessage(message, keys[i]))
                ret = "failure";
        if (!c.connection.postMessage(message, Crypto.getPublicKey()))
            ret = "failure";
        Parser.parse(message, c.db);
        return ret;
    }
    
    /** Change whether or not a category can see your profile information.
     * \param category The name of the category you wish to update.
     * \param value The desired value, true to allow the category to see your
     *  profile data, false to forbid it.
     * \return "true" if successful, "false" otherwise.
     */
    public String updatePDATApermission (String category, boolean value) {
        Logger.write("VERBOSE", "TnImpl", "updatePDATApermission(" + category + ", " + value + ")");
        String ret = "success";
        
        Message msg = new MessageFactory().newUPDATECAT(category, value);
        ret = c.connection.postMessage(msg, Crypto.getPublicKey())?"success":"failure";
        if (!c.db.updatePDATApermission(category, value))
            ret = "failure";
        if (value) {
            PublicKey[] keys = c.db.getCategoryMembers(category);
            for (int i = 0; i < keys.length; i++) {
                if(!sendPDATA(Crypto.encodeKey(keys[i])).equals("success"))
                    ret = "failure";
            }
        }
        Parser.parse(msg, c.db);
        
        return ret;
    }
    
    /** Create a new conversation.
     * \param keys The keys of each person you wish to include in the
     *   conversation.
     * \return "true" if successful, "false" otherwise.
     */
    public String[] createCHAT (String[] keys) {
        Logger.write("INFO", "TnImpl", "createCHAT(<" + keys.length + " keys>)");
        String[] ret = new String[2];
        ret[0] = "success";
        
        String myStrKey = Crypto.encodeKey(Crypto.getPublicKey());
        int count = 0;
        int index = 0;
        for (int i=0; i < keys.length; i++) {
            if (keys[i].equals(myStrKey)) {
                count++;
                index = i;
            }
        }
        
        //add self, or remove double self, from convo participants list
        String[] newKeys = null;
        if (count == 0) {
            newKeys = new String[keys.length+1];
            for (int i=0; i < keys.length; i++)
                newKeys[i] = keys[i];
            newKeys[keys.length] = myStrKey;
            keys = newKeys;
        } else if (count == 2) {
            newKeys = new String[keys.length-1];
            int j = 0; //javac complains about `for (int i=0, int j=1;...' for some reason
            for (int i=0; i < keys.length; i++)
                if (i != index)
                    newKeys[j++] = keys[i];
            keys = newKeys;
        }
        
        Message msg = new MessageFactory().newCHAT(keys);
        for (int i = 0; i < keys.length; i++)
            c.connection.postMessage(msg, Crypto.decodeKey(keys[i]));
        Parser.parse(msg, c.db);
        
        Logger.write("VERBOSE", "TnImpl", "createCHAT returning " + msg.getSig());
        ret[1] = msg.getSig();
        return ret;
    }
    
    /** Add a message to a conversation.
     * \param text The text of your message.
     * \param sig The signature of the conversation you wish to add a message to.
     * \return "true" if successful, "false" otherwise.
     */
    public String addMessageToCHAT (String text, String sig) {
        Logger.write("INFO", "TnImpl", "addMessageToCHAT(" + text + ",...)");
        PublicKey[] keys = c.db.getPeopleInConvo(sig);
        String ret = "success";
            
        if (keys.length == 0) {
            Logger.write("INFO", "TnImpl", "addMessageToCHAT(...) convo has " + Integer.toString(keys.length) + " participants");
            return "failure"; //Convo doesn't exist, or we don't know about it yet
        }
        
        Logger.write("INFO", "TnImpl", "addMessageToCHAT(...) convo has " + Integer.toString(keys.length) + " participants");
        Message msg = new MessageFactory().newPCHAT(sig, text);
        for (int i = 0; i < keys.length; i++)
            if (!c.connection.postMessage(msg, keys[i]))
                ret = "failure";
        Parser.parse(msg, c.db);
        return ret;
    }
    
    /** Add a like to a post or comment.
     * \param sig The signature of the post or comment you wish to add a like to.
     * \return "true" if successful, "false" otherwise.
     */
    public String like (String sig) {
        Logger.write("VERBOSE", "TnImpl", "like(...)");
        PublicKey[] visibleTo = c.db.getVisibilityOfParent(sig);
        Message message = new MessageFactory().newLIKE(sig);
        String ret = "success";
        
        for (int i = 0; i < visibleTo.length; i++)
            if (!c.connection.postMessage(message, visibleTo[i]))
                ret = "failure";
        if (!c.connection.postMessage(message, Crypto.getPublicKey()))
            ret = "failure";
        Parser.parse(message, c.db);
            
        return ret;
    }
    
    /** Remove a like from a post or comment.
     * \param sig The signature of the post or comment you wish to remove a like from.
     * \return "true" if successful, "false" otherwise.
     */
    public String unlike (String sig) {
        Logger.write("VERBOSE", "TnImpl", "unlike(...)");
        PublicKey[] visibleTo = c.db.getVisibilityOfParent(sig);
        Message message = new MessageFactory().newUNLIKE(sig);
        String ret = "success";
        
        for (int i = 0; i < visibleTo.length; i++)
            if (!c.connection.postMessage(message, visibleTo[i]))
                ret = "failure";
        if(!c.connection.postMessage(message, Crypto.getPublicKey()))
            ret = "failure";
        Parser.parse(message, c.db);
            
        return ret;
    }
    
    /** Add a category.
     * By default the category cannot see your profile information.
     * \param name The name of the category you wish to create.
     * \return "true" if successful, "false" otherwise.
     */
    public String addCategory (String name) {
        Logger.write("VERBOSE", "TnImpl", "addCategory(" + name + ")");
        Message msg = new MessageFactory().newADDCAT(name, false);
        
        return (c.db.addCategory(name, false) &&
                c.connection.postMessage(msg, Crypto.getPublicKey()))
        ?"success":"failure";
    }
    
    /** Add a user to a category.
     * \param group The name of the category you wish to add a user to.
     * \param key The key you wish to add to the category.
     * \return "true" if successful, "false" otherwise.
     */
    public String addToCategory (String group, String key) {
        Logger.write("VERBOSE", "TnImpl", "addToCategory(" + group + ",...)");
        
        boolean alreadyMember = false;
        PublicKey[] members = c.db.getCategoryMembers(group);
        for (int i = 0; i < members.length; i++)
            if (members[i].equals(Crypto.decodeKey(key)))
                alreadyMember = true;
        
        if (!alreadyMember) {
            if (c.db.addToCategory(group, Crypto.decodeKey(key))) {
                Message msg = new MessageFactory().newADDTOCAT(group, key);
                c.connection.postMessage(msg, Crypto.getPublicKey());
                if (c.db.canSeePDATA(group)) {
                    return sendPDATA(key).equals("success") ? "success" : "failure";
                } else {
                    return "success";
                }
                
                //We do not retroactivly send people posts/comments/likes because
                //  people will forget what they've posted in the past and accidently
                //  share it with new contacts.
            } else {
                return "failure";
            }
        } else {
            Logger.write("WARNING", "TnImpl", "Duplicate entry to tCategoryMembers prevented");
            return "failure";
        }
    }
    
    /** Send profile information to the specified key.
     * This is a one off thing, the user will not be automatically kept abreast
     * of new profile information.
     * \param key The public key of the user you wish to send profile information to.
     * \return "true" if successful, "false" otherwise.
     */
    public String sendPDATA (String key) {
        String[] values = {"email", "name", "gender", "birthday"};
        String[] fields = {getMyPDATA("email"), getMyPDATA("name"), getMyPDATA("gender"), getMyPDATA("birthday")};
        return c.connection.postMessage(new MessageFactory().newPDATA(fields, values),
                                        Crypto.decodeKey(key))
               ? "success" : "failure";
    }
    
    /** Remove a user from a category.
     * \param group The name of the category you wish to remove a person from.
     * \param key The public key of the person you wish to be removed.
     * \return "true" if successful, "false" otherwise.
     */
    public String removeFromCategory (String group, String key) {
        Logger.write("VERBOSE", "TnImpl", "removeFromCategory(" + group + ",...)");
        Message msg = new MessageFactory().newREMFROMCAT(group, key);
        c.connection.postMessage(msg, Crypto.getPublicKey());
        return c.db.removeFromCategory(group, Crypto.decodeKey(key))?"success":"failure";
    }
    
    /** Add a public key.
     * \param key The key you wish to add.
     * \return "true" if successful, "false" otherwise.
     */
    public String addKey (String key) {
        Logger.write("VERBOSE", "TnImpl", "addKey(...)");
        Message msg = new MessageFactory().newADDKEY(key);
        return (c.db.addKey(Crypto.decodeKey(key)) &&
                c.connection.postMessage(msg, Crypto.getPublicKey())) ? "success":"failure";
    }
    
    /** Add a post to a specified wall.
     * Posting on another users wall does not require their permission.
     * \param wallKey The key of the user whos wall you want to post on.
     * \param categoryVisibleTo The name of the category of people who may see
     *    the post.
     * \param msg The text of the post you wish to make.
     * \return "true" if successful, "false" otherwise.
     */
    public String addPost (String wallKey, String categoryVisibleTo, String msg) {
        Logger.write("VERBOSE", "TnImpl", "addPost(..., " + msg + ")");
        PublicKey[] visibleTo = c.db.getCategoryMembers(categoryVisibleTo);
        String[] visibleToStr = new String[visibleTo.length];
        String ret = "success";
        
        for (int i = 0; i < visibleTo.length; i++)
            visibleToStr[i] = Crypto.encodeKey(visibleTo[i]);
        Message message = new MessageFactory().newPOST(msg, wallKey, visibleToStr);
        
        for (int i = 0; i < visibleTo.length; i++)
            if (!c.connection.postMessage(message, visibleTo[i]))
                ret = "failure";
        if (!c.connection.postMessage(message, Crypto.getPublicKey()))
            ret = "failure";
        Parser.parse(message, c.db);
            
        return ret;
    }
    
    /** Add a comment to a specified comment or post.
     * Commenting does not require the permission of the person who posted the
     * item you are commenting.
     * \param parent The signature of the post or comment that you wish to
     *   comment on.
     * \param text The text of the comment you wish to make.
     * \return "true" if successful, "false" otherwise.
     */
    public String addComment (String parent, String text) {
        Logger.write("VERBOSE", "TnImpl", "addComment(..., " + text + ")");
        PublicKey[] visibleTo = c.db.getVisibilityOfParent(parent);
        Message message = new MessageFactory().newCMNT(parent, text);
        String ret = "success";
        
        Logger.write("VERBOSE", "TnImpl", "==================POSTING COMMENT TO " + visibleTo.length + " people");
        
        for (int i = 0; i < visibleTo.length; i++)
            if (!c.connection.postMessage(message, visibleTo[i]))
                ret = "failure";
        if(!c.connection.postMessage(message, Crypto.getPublicKey()))
            ret = "failure";
        Parser.parse(message, c.db);
            
        return ret;
    }
    
    /** Revoke the current users key.
     * This marks the current users key as untrusted. All people whose key they
     * have will be informed. This cannot be publically broadcast to all users
     * because despite the nature of misdirection employed it would be fairly
     * easy for the server operators to identify and suppress the revocation
     * message.
     * \warning Only people whose keys the user has added will be informed of
     * the revocation.
     * \warning This erases the users account and local database.
     * \return "true" if successful, "false" otherwise.
     */
    public String revokeMyKey () {
        Logger.write("VERBOSE", "TnImpl", "-------revokeMyKey()-------");
        PublicKey[] keys = c.db.getCategoryMembers("all");
        String ret = "success";
        
        for (int i = 0; i < keys.length; i++)
            if (!c.connection.postMessage(new MessageFactory().newREVOKE(0), keys[i])) //Can't be sent in cleartext, serverops could suppress it
                ret = "failure";
        
        //erase db and keypair
        new File(Database.path + "/lastread").delete();
        new File(Database.path + "/public.key").delete();
        new File(Database.path + "/private.key").delete();
        new File(Database.path + "/turtlenet.db").delete();
        new File(Database.path).delete();
        
        return ret;
    }
}
