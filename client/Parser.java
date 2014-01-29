//All methods ought to be static
class Parser {
    public static void handle (Message msg, Database db) {
        if (msg.getCmd().equals("POST")) {
            db.addPost(msg);
        } else if (msg.getCmd().equals("CLAIM")) {
            db.addClaim(msg);
        } else if (msg.getCmd().equals("NULL")) {
            System.out.println("WARNING: NULL Message type parsed, this should never happen");
        }
    }
}
