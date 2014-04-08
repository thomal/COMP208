//All methods ought to be static
//Most real parsing occurs in the Message class, this just passes commands to DB
package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;

class Parser {
    public static void parse (Message msg, Database db) {
        if (msg.getCmd().equals("POST"))        //post to own wall
            db.addPost(msg);
        else if (msg.getCmd().equals("CLAIM"))  //claim a username
            db.addClaim(msg);
        else if (msg.getCmd().equals("REVOKE")) //revoke private key
            db.addRevocation(msg);
        else if (msg.getCmd().equals("PDATA"))  //create or update profile data
            db.addPdata(msg);
        else if (msg.getCmd().equals("CHAT"))   //n-n chat
            db.addChat(msg);
        else if (msg.getCmd().equals("PCHAT"))  //1-1 chat
            db.addPChar(msg);
        else if (msg.getCmd().equals("FPOST"))  //someone posting to your wall
            db.addFPost(msg);
        else if (msg.getCmd().equals("CMNT"))   //comment
            db.addComment(msg);
        else if (msg.getCmd().equals("LIKE"))   //like
            db.addLike(msg);
        else if (msg.getCmd().equals("EVNT"))   //event
            db.addEvent(msg);
        //else if (msg.getCmd().equals("NULL"))
            //undecryptable
    }
}
