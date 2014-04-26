package ballmerpeak.turtlenet.shared;

import java.io.Serializable;

public class Conversation implements Serializable {
    public Conversation () {
        signature = "<SIGNATURE NOT KNOWN>";
        timestamp = "0";
        firstMessage = "<FIRST MESSAGE NOT KNOWN>";
        users = new String[0];
        keys = new String[0];
    }

    public Conversation (String sig, String time, String fmsg, String[] _users, String[] _keys) {
        signature = sig;
        timestamp = time;
        firstMessage = fmsg;
        users = _users;
        keys = _keys;
    }
    
    public String concatNames() {
        String names = "";
        for (int i = 0; i < users.length; i++)
            names += users[i] + " ";
        return names;
    }
    
    public String signature;
    public String timestamp;
    public String firstMessage;
    public String[] users; //usernames
    public String[] keys;  //keys[0] is the key of users[0], etc.
}
