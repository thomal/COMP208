//Having an "Author" field would be reduncant and untrusted
import java.util.StringTokenizer;

public class Message {
    public Message (String cmd, String _content, long timeCreated, String RSAsig) {
        command   = cmd;
        content   = _content;
        signature = RSAsig;
        timestamp = timeCreated;
    }
    
    /* "POST\520adfc4\Hello, World!\123" -> new Message("POST", "Hello, World!", "520adfc4", 123) */
    public static Message parse (String msg) {
        String[] tokens = new String[4];
        StringTokenizer tokenizer = new StringTokenizer(msg, "\\", false);
        tokens[0] = tokenizer.nextToken(); //command
        tokens[1] = tokenizer.nextToken(); //signature
        tokens[2] = tokenizer.nextToken(); //message content
        tokens[3] = tokenizer.nextToken(); //timestamp
        long ts = Long.parseLong(tokens[3]);
        
        return new Message(tokens[0], tokens[2], ts, tokens[1]);
    }
    
    public String toString () {
        return command + "\\" + signature + "\\" + content + "\\" + timestamp;
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
    
    public long getTimestamp () {
        return timestamp;
    }
    
    private String command;
    private String content;
    private String signature;
    private long   timestamp;
}
