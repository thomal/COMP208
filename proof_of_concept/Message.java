public class Message {
    public Message (String cmd, String con, String sig) {
        command = cmd;
        content = con;
        signature = sig;
    }
    
    /* "POST\520adfc4\Hello, World!" -> new Message("POST", "Hello, World!", "520adfc4") */
    public static Message parse (String msg) {
        String cmd = msg.substring(0, msg.indexOf("\\"));
        String p2 = msg.substring(msg.indexOf("\\")+1);
        String sig = p2.substring(0, p2.indexOf("\\"));
        String text = p2.substring(p2.indexOf("\\")+1);
        return new Message(cmd, text, sig);
    }
    
    public String toString () {
        return command + "\\" + signature + "\\" + content;
    }
    
    public String getCmd () {
        return command;
    }
    
    public String getContent () {
        return content;
    }
    
    public String getSig () {
        return signature;
    }
    
    private String command;
    private String content;
    private String signature;
}
