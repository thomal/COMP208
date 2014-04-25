package ballmerpeak.turtlenet.shared;

import ballmerpeak.turtlenet.shared.Tokenizer;
import java.security.*;
import java.io.Serializable;
import java.util.Arrays;

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
        command = "NULL";
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
        Tokenizer tokenizer = new Tokenizer(content, ':');
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        return colonPairs[colonPairs.length-1];
    }
    
    public String[] POSTgetVisibleTo() {
        Tokenizer tokenizer = new Tokenizer(content, ':');
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        return Arrays.copyOfRange(colonPairs, 0, colonPairs.length-1);
    }
    
    public String FPOSTgetText() {
        Tokenizer tokenizer = new Tokenizer(content, ':');
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        return colonPairs[colonPairs.length-1];
    }
    
    public String FPOSTgetWall() {
        Tokenizer tokenizer = new Tokenizer(content, ':');
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        return colonPairs[0];
    }
    
    public String[] FPOSTgetVisibleTo() {
        Tokenizer tokenizer = new Tokenizer(content, ':');
        String[] colonPairs = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++)
            colonPairs[i] = tokenizer.nextToken();
        return Arrays.copyOfRange(colonPairs, 1, colonPairs.length-1);
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
    // returns an array of strings and now of keys because of GWT,
    //   Crypto.decodeKey should be used to turn each string into a key
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
    /* there cannot be a REVOKEgetKey due to GWT */
    public long REVOKEgetTime() {
        try {
            return Long.parseLong(content);
        } catch (Exception e) {
            //Invalid timestamp
            return -1;
        }
    }
    
    public String command;
    public String content;
    public String signature;
    public long timestamp;
}

