package ballmerpeak.turtlenet.shared;

import ballmerpeak.turtlenet.shared.Tokenizer;
import java.security.*;
import java.io.Serializable;

public class Message implements Serializable {
    //You shouldn't use this, rather use MessageFactory.newMessage(command, data)
    //GWT cannot use the factory, it shouldn't construct messages but pass their
    //    data as arguments to whatever needs it. Maybe have an async factory?
    public Message (String cmd, String _content, long timeCreated, String RSAsig) {
        command   = cmd;
        content   = _content;
        signature = RSAsig;
        timestamp = timeCreated;
    }
    
    public Message () {
        command = "NOP";
        content = "";
        signature = "";
        timestamp = -1;
    }
    
    /* "POST\520adfc4\Hello, World!\123" -> new Message("POST", "Hello, World!", "520adfc4", 123) */
    public static Message parse (String msg) {
        String[] tokens = new String[4];
        Tokenizer tokenizer = new Tokenizer(msg, '\\');
        tokens[0] = tokenizer.nextToken(); //command
        tokens[1] = tokenizer.nextToken(); //signature
        tokens[2] = msg.substring(msg.indexOf("\\", msg.indexOf("\\",0)+1)+1, msg.lastIndexOf("\\")); //message content
        tokens[3] = msg.substring(msg.lastIndexOf("\\")+1); //timestamp
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
    public String POSTgetText() {
        return content;
    }
    
    public String CLAIMgetName() {
        return content;
    }
    
    //content in form "field1:value1;field2:value2;"
    public String[][] PDATAgetValues() {
        //Split into colon pairs, semicolon delimiter
        Tokenizer tokenizer = new Tokenizer(content, ';');
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        
        //split into field/value pairs, colon delimiter
        String[][] values = new String[colonPairs.length][2];
        for (int i = 0; i < colonPairs.length; i++) {
            Tokenizer st = new Tokenizer(colonPairs[i], ':');
            values[i][0] = st.nextToken();
            values[i][1] = st.nextToken();
        }
        
        return values;
    }
    
    /* establish a chat and the people in it, without any messages */
    public String[] CHATgetKeys() {
        Tokenizer st = new Tokenizer(content, ':');
        String[] keys = new String[st.countTokens()];
        for (int i = 0; i < keys.length; i++)
            keys[i] = st.nextToken();
        return keys;
    }
    
    /* PCHAT adds messages to a conversation */
    /* returns <conversation ID, messageText> */
    public String PCHATgetText() {
        Tokenizer st = new Tokenizer(content, ':');
        String convoID = st.nextToken();
        String text    = st.nextToken();
        return text;
    }
    
    public String PCHATgetConversationID() {
        Tokenizer st = new Tokenizer(content, ':');
        String convoID = st.nextToken();
        String text    = st.nextToken();
        return convoID;
    }
    
    public String CMNTgetText() {
        Tokenizer st = new Tokenizer(content, ':');
        String itemID  = st.nextToken();
        String text    = st.nextToken();
        return text;
    }
    
    public String CMNTgetItemID() {
        Tokenizer st = new Tokenizer(content, ':');
        String itemID  = st.nextToken();
        String text    = st.nextToken();
        return itemID;
    }
    
    public String LIKEgetItemID() {
        return content;
    }
    
    public String EVNTgetName() {
        Tokenizer st = new Tokenizer(content, ':');
        long start  = Long.parseLong(st.nextToken());
        long end    = Long.parseLong(st.nextToken());
        String name = st.nextToken();
        return name;
    }
    
    public long EVNTgetStart() {
        Tokenizer st = new Tokenizer(content, ':');
        long start  = Long.parseLong(st.nextToken());
        long end    = Long.parseLong(st.nextToken());
        String name = st.nextToken();
        return start;
    }
    
    public long EVNTgetEnd() {
        Tokenizer st = new Tokenizer(content, ':');
        long start  = Long.parseLong(st.nextToken());
        long end    = Long.parseLong(st.nextToken());
        String name = st.nextToken();
        return end;
    }
    
    /* time of revocation, not timestamp of message */
    public long REVOKEgetTime() {
        try {
            return Long.parseLong(content);
        } catch (Exception e) {
            //Invalid timestamp
            return -1;
        }
    }
    
    private String command;
    private String content;
    private String signature;
    private long   timestamp;
}

