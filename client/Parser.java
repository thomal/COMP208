//All methods ought to be static
class Parser {
    public static void handle (Message msg, Database db) {
        System.out.println("Parsing a " + msg.getCmd());
        if (msg.getCmd().equals("POST")) {
                db.addPost(msg);
        } else if (msg.getCmd().equals("CLAIM")) {
            System.out.println("UNIMPLEMENTED: Parse CLAIM messages");
        } else if (msg.getCmd().equals("NULL")) {
            //do nothing
        }
    }
}
