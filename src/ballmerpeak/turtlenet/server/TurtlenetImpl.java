package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.*;
import java.security.*;
import ballmerpeak.turtlenet.server.TNClient;
import ballmerpeak.turtlenet.server.MessageFactoryImpl;
import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.shared.Conversation;

@SuppressWarnings("serial")
public class TurtlenetImpl extends RemoteServiceServlet implements Turtlenet {
    TNClient c;
    
    public String startTN(String password) {
        Logger.write("INFO", "TNImpl","startTN()");
        c = new TNClient(password);
        if (c != null) {
            Thread t = new Thread(c);
            t.start();
            return "success";
        } else {
            return "failure";
        }
    }
    
    public String stopTN() {
        Logger.write("INFO", "TNImpl","stopTN()");
        c.running = false;
        return "success";
    }
    
    //Profile Data
    public String getMyUsername() {
        return c.db.getName(Crypto.getPublicKey());
    }
    
    public String getUsername(String key) {
        String name = c.db.getName(Crypto.decodeKey(key));
        Logger.write("VERBOSE", "TNImpl","getUsername returning \"" + name + "\"");
        return name;
    }
    
    public String getMyPDATA(String field) {
        return getPDATA(field, Crypto.encodeKey(Crypto.getPublicKey()));
    }
    
    public String getPDATA(String field, String key) {
        return c.db.getPDATA(field, Crypto.decodeKey(key));
    }
    
    public String getMyKey() {
        return Crypto.encodeKey(Crypto.getPublicKey());
    }
    
    public String getKey(String username) {
        return Crypto.encodeKey(c.db.getKey(username));
    }
    
    public String[][] getCategories () {
        return c.db.getCategories();
    }
    
    public String[][] getPeople () {
        return getCategoryMembers("all");
    }
    
    public Conversation[] getConversations () {
        return c.db.getConversations();
    }
    
    public Conversation getConversation (String sig) {
        return c.db.getConversation(sig);
    }
    
    public String[][] getConversationMessages (String sig) {
        return c.db.getConversationMessages(sig);
    }
    
    public String[][] getCategoryMembers (String category) {
        PublicKey[] keys = c.db.getCategoryMembers(category);        
        String[][] pairs = new String[keys.length][2];
        
        for (int i = 0; i < keys.length; i++) {
            pairs[i][0] = c.db.getName(keys[i]);
            pairs[i][1] = Crypto.encodeKey(keys[i]);
        }
        
        return pairs;
    }
    
    
    //Profile Data
    public String claimUsername (String uname) {
        if(c.connection.claimName(uname))
            return "success";
        else
            return "failure";
    }
    
    public String updatePDATA (String field, String value) {
        c.connection.postMessage(new MessageFactoryImpl().newPDATA(field, value),
                                 Crypto.getPublicKey());
        return "success";
    }
    
    public String updatePDATApermission (String category, boolean value) {
        c.db.updatePDATApermission(category, value);
        return "success";
    }
    
    //Posting
    public String[] createCHAT (String[] keys) {
        Message msg = new MessageFactoryImpl().newCHAT(keys);
        for (int i = 0; i < keys.length; i++)
            c.connection.postMessage(msg, Crypto.decodeKey(keys[i]));
        String[] ret = new String[2];
        ret[0] = "success";
        ret[1] = msg.getSig();
        return ret;
    }
    
    public String addMessageToCHAT (String text, String sig) {
        Logger.write("INFO", "TnImpl", "addMessageToCHAT(" + text + "," + sig + ")");
        PublicKey[] keys = c.db.getPeopleInConvo(sig);
        Message msg = new MessageFactoryImpl().newPCHAT(sig, text);
        for (int i = 0; i < keys.length; i++)
            c.connection.postMessage(msg, keys[i]);
        return "success";
    }
    
    //Friends
    public String addCategory (String name) {
        if (c.db.addCategory(name, false))
            return "success";
        else
            return "failure";
    }
    
    public String addToCategory (String group, String key) {
        Logger.write("INFO", "TnImpl", "addToCategoru(" + group + "," + key + ")");
        
        if (c.db.addToCategory(group, Crypto.decodeKey(key)))
            return "success";
        else
            return "failure";
    }
    
    public String addKey (String key) {
        if (c.db.addKey(Crypto.decodeKey(key)))
            return "success";
        else
            return "failure";
    }
}
