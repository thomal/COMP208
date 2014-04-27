package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.*;
import java.security.*;
import ballmerpeak.turtlenet.server.TNClient;
import ballmerpeak.turtlenet.server.MessageFactoryImpl;
import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.shared.Conversation;
import ballmerpeak.turtlenet.shared.PostDetails;
import ballmerpeak.turtlenet.shared.CommentDetails;

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
    
    public PostDetails[] getWallPosts (String key) {
        Message[] msgs = c.db.getWallPost(Crypto.decodeKey(key));
        PostDetails[] posts = new PostDetails[msgs.length];
        for (int i = 0; i < msgs.length; i++) {
            String sig = msgs[i].getSig();
            boolean liked = c.db.isLiked(sig);
            int commentCount = c.db.getComments(sig).length;
            Long time = msgs[i].getTimestamp();
            String username = c.db.getName(c.db.getSignatory(msgs[i]));
            String text = msgs[i].POSTgetText();
            
            posts[i] = new PostDetails(sig, liked, commentCount, time, username, text, Crypto.encodeKey(c.db.getSignatory(msgs[i])));
        }
        return posts;
    }
    
    public CommentDetails[] getComments (String parent) {
        Message[] commentMsgs = c.db.getComments(parent);
        CommentDetails[] details = new CommentDetails[commentMsgs.length];
        
        for (int i = 0; i < commentMsgs.length; i++) {
            CommentDetails thisCmnt = new CommentDetails();
            thisCmnt.posterKey = Crypto.encodeKey(c.db.getSignatory(commentMsgs[i]));
            thisCmnt.posterName = c.db.getName(Crypto.decodeKey(thisCmnt.posterKey));
            thisCmnt.sig = commentMsgs[i].getSig();
            thisCmnt.text = commentMsgs[i].CMNTgetText();
            thisCmnt.liked = c.db.isLiked(parent);
            details[i] = thisCmnt;
        }
        return details;
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
        Logger.write("INFO", "TnImpl", "createCHAT(<" + keys.length + " keys>)");
        Message msg = new MessageFactoryImpl().newCHAT(keys);
        for (int i = 0; i < keys.length; i++)
            c.connection.postMessage(msg, Crypto.decodeKey(keys[i]));
        String[] ret = new String[2];
        ret[0] = "success";
        ret[1] = msg.getSig();
        return ret;
    }
    
    public String addMessageToCHAT (String text, String sig) {
        Logger.write("INFO", "TnImpl", "addMessageToCHAT(" + text + ",...)");
        PublicKey[] keys = c.db.getPeopleInConvo(sig);
        
        if (keys.length == 0) {
            try {
                Thread.sleep(5*1000); //Account for latency when creating a chat
            } catch (Exception e) {
                Logger.write("ERROR", "TnImpl", "addMessageToCHAT(...) Could not find convo");
            }
            
            keys = c.db.getPeopleInConvo(sig);
            if (keys.length == 0) {
                Logger.write("INFO", "TnImpl", "addMessageToCHAT(...) convo has " + Integer.toString(keys.length) + " participants");
                return "failure"; //Convo doesn't exist, or we don't know about it yet
            }
        }
        
        Logger.write("INFO", "TnImpl", "addMessageToCHAT(...) convo has " + Integer.toString(keys.length) + " participants");
        Message msg = new MessageFactoryImpl().newPCHAT(sig, text);
        for (int i = 0; i < keys.length; i++)
            c.connection.postMessage(msg, keys[i]);
        return "success";
    }
    
    public String like (String sig) {
        PublicKey[] visibleTo = c.db.getVisibilityOfParent(sig);
        Message message = new MessageFactoryImpl().newLIKE(sig);
        
        for (int i = 0; i < visibleTo.length; i++)
            c.connection.postMessage(message, visibleTo[i]);
            
        return "success";
    }
    
    public String unlike (String sig) {
        PublicKey[] visibleTo = c.db.getVisibilityOfParent(sig);
        Message message = new MessageFactoryImpl().newUNLIKE(sig);
        
        for (int i = 0; i < visibleTo.length; i++)
            c.connection.postMessage(message, visibleTo[i]);
            
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
        
        boolean alreadyMember = false;
        PublicKey[] members = c.db.getCategoryMembers(group);
        for (int i = 0; i < members.length; i++)
            if (members[i].equals(Crypto.decodeKey(key)))
                alreadyMember = true;
        
        if (!alreadyMember) {
            if (c.db.addToCategory(group, Crypto.decodeKey(key)))
                return "success";
            else
                return "failure";
        } else {
            Logger.write("WARNING", "TnImpl", "Duplicate entry to tCategoryMembers prevented");
            return "failure";
        }
    }
    
    public String addKey (String key) {
        if (c.db.addKey(Crypto.decodeKey(key)))
            return "success";
        else
            return "failure";
    }
    
    public String addPost (String wallKey, String categoryVisibleTo, String msg) {
        PublicKey[] visibleTo = c.db.getCategoryMembers(categoryVisibleTo);
        String[] visibleToStr = new String[visibleTo.length];
        
        for (int i = 0; i < visibleTo.length; i++)
            visibleToStr[i] = Crypto.encodeKey(visibleTo[i]);
        Message message = new MessageFactoryImpl().newPOST(msg, wallKey, visibleToStr);
        
        for (int i = 0; i < visibleTo.length; i++)
            c.connection.postMessage(message, visibleTo[i]);
        c.connection.postMessage(message, Crypto.getPublicKey());
            
        return "success";
    }
    
    public String addComment (String parent, String text) {
        PublicKey[] visibleTo = c.db.getVisibilityOfParent(parent);
        Message message = new MessageFactoryImpl().newCMNT(parent, text);
        
        for (int i = 0; i < visibleTo.length; i++)
            c.connection.postMessage(message, visibleTo[i]);
            
        return "success";
    }
}
