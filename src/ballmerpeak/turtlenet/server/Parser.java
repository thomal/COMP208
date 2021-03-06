//All methods ought to be static
//Most real parsing occurs in the Message class, this just passes commands to DB
package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;

public class Parser {
    /* Useful to ID the type of message on behalf of the DB so it can use type
     * specific get methods (e.g.: Message.PCHATgetConversationID()). Most
     * parsing actually occurs in the Message class itself. Maybe this should
     * be changed so parsing occurs here, e.g.: Parser.LIKEgetItemID(msg), but
     * msg.LIKEgetItemID() is more natual.
     */
    public static void parse (Message msg, Database db) {
    
        Logger.write("VERBOSE", "PARSE", "parsing message");
        
        escape(msg);
        
        if (db.isRevoked(db.getSignatory(msg)))
            Logger.write("WARNING", "PARSE", "Revoked key in use, message dropped");
        else if (msg.getCmd().equals("POST"))       //post to own wall
            db.addPost(msg);
        else if (msg.getCmd().equals("CLAIM"))      //claim a username
            db.addClaim(msg);
        else if (msg.getCmd().equals("REVOKE"))     //revoke private key
            db.addRevocation(msg);
        else if (msg.getCmd().equals("PDATA"))      //create or update profile data
            db.addPDATA(msg);
        else if (msg.getCmd().equals("CHAT"))       //establish chat
            db.addConvo(msg);
        else if (msg.getCmd().equals("PCHAT"))      //add message to chat
            db.addMessageToChat(msg);
        else if (msg.getCmd().equals("CMNT"))       //comment
            db.addComment(msg);
        else if (msg.getCmd().equals("LIKE"))       //like
            db.addLike(msg);
        else if (msg.getCmd().equals("UNLIKE"))     //like
            db.unlike(msg.UNLIKEgetItemID());
        else if (msg.getCmd().equals("EVNT"))       //event
            db.addEvent(msg);
        else if (msg.getCmd().equals("ADDCAT"))     //add category
            db.addCategory(msg);
        else if (msg.getCmd().equals("UPDATECAT"))  //update categorys canSeePDATA
            db.updatePDATApermission(msg);
        else if (msg.getCmd().equals("ADDTOCAT"))   //add key to category
            db.addToCategory(msg);
        else if (msg.getCmd().equals("REMFROMCAT")) //remove key from category
            db.removeFromCategory(msg);
        else if (msg.getCmd().equals("ADDKEY"))     //add public key
            db.addKey(msg);
        else if (msg.getCmd().equals("NULL"))
            Logger.write("VERBOSE", "PARSE", "undecryptable message"); //not for us
        else if (!msg.getCmd().equals("FPOST"))
            Logger.write("ERROR", "PARSE", "Unknown message type: \"" + msg.getCmd() + "\"");
        
        if (msg.getCmd().equals("FPOST"))
            Logger.write("WARNING", "PARSE", "FPOST is depreciated");
    }
    
    private static void escape (Message m) {
        m.content = m.content.replace("'", "''");
    }
}
