//Having an "Author" field would be redundant and untrusted
package ballmerpeak.turtlenet.shared;

import java.util.StringTokenizer;
import ballmerpeak.turtlenet.server.*;
import java.security.*;

public class Message {
    public Message (String cmd, String _content, long timeCreated, String RSAsig) {
        command   = cmd;
        content   = _content;
        signature = RSAsig;
        timestamp = timeCreated;
    }
    
    /* "POST\520adfc4\Hello, World!\123" -> new Message("POST", "Hello, World!", "520adfc4", 123) */
    public static Message parse (String msg) {
        msg = Parser.escape(msg);
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
    
    /* universal */
    public String getCmd () {
        return command;
    }
    
    public String getSig () {
        return signature;
    }
    
    public String getContent () {
        return content;
    }
    
    public long getTimestamp () {
        return timestamp;
    }
    
    /* type specific */
    /* inband backslashes are escaped with backslashes */
    public String POSTgetText() {
        return content;
    }
    
    public String CLAIMgetName() {
        return content;
    }
    
    //content in form "field1:value1;field2:value2;"
    public String[][] PDATAgetValues() {
        //Split into colon pairs, semicolon delimiter
        StringTokenizer tokenizer = new StringTokenizer(content, ";", false);
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        
        //split into field/value pairs, colon delimiter
        String[][] values = new String[colonPairs.length][2];
        for (int i = 0; i < colonPairs.length; i++) {
            StringTokenizer st = new StringTokenizer(colonPairs[i], ":", false);
            values[i][0] = st.nextToken();
            values[i][1] = st.nextToken();
        }
        
        return values;
    }
    
    //TODO//////////////////////////////////////////////////////////////////////
    //PCHAT
    //FPOST
    //CMNT
    //LIKE
    //EVNT
    
    /* time of revocation, not timestamp of message */
    public long REVOKEgetTime() {
        try {
            return Long.parseLong(content);
        } catch (Exception e) {
            System.out.println("ERROR: Invalid timestamp");
            return -1;
        }
    }
    
    public PublicKey REVOKEgetKey(Database db) {
        return db.getSignatoryKey(this);
    }
    
    private String command;
    private String content;
    private String signature;
    private long   timestamp;
}

