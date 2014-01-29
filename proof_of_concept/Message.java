//Having an "Author" field would be reduncant and untrusted
import java.util.StringTokenizer;

public class Message {
    public Message (String cmd, String con, String sig) {
        command = cmd;
        content = con;
        signature = sig;
    }
    
    /* "POST\520adfc4\Hello, World!" -> new Message("POST", "Hello, World!", "520adfc4") */
    public static Message parse (String msg) {
        String[] tokens = new String[3];
        StringTokenizer tokenizer = new StringTokenizer(msg, "\\", false);
        tokens[0] = tokenizer.nextToken();
        tokens[1] = tokenizer.nextToken();
        tokens[2] = tokenizer.nextToken();
        
        return new Message(tokens[0], tokens[2], tokens[1]);
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
