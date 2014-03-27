//All methods ought to be static
package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;

class Parser {
    public static void parse (Message msg, Database db) {
        if (msg.getCmd().equals("POST")) {
            db.addPost(msg);
        } else if (msg.getCmd().equals("CLAIM")) {
            db.addClaim(msg);
        } else if (msg.getCmd().equals("NULL")) {
            //undecryptable
        }
    }
}
