//All methods ought to be static
class Parser {
    public static void handle (String msg, Database db) {
        if (msg.length() > 6) {
            if (msg.substring(0,5).equals("POST\\")) {
                db.addPost(msg.substring(5));
            }
        }
    }
}
