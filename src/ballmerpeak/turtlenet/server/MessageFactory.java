//Necceccitated as not being another Message constructor by GWT

package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.server.Crypto;
import java.security.*;

public class MessageFactory {
    public static Message newMessage(String cmd, String content) {
        long timestamp = System.currentTimeMillis();
        return new Message(cmd, content, System.currentTimeMillis(), Crypto.sign(timestamp + content));
    }
    
    public static Message newCLAIM(String username) {
        return newMessage("CLAIM", username);
    }
    
    public static Message newREVOKE(long time) {
        return newMessage("REVKOE", ""+time);
    }
    
    public static Message newPDATA(String field, String value) {
        return newMessage("PDATA", field + ":" + value + ";");
    }
    
    public static Message newCHAT(PublicKey[] keys) {
        String keyString = "";
        String delim = "";
        for (int i = 0; i < keys.length; i++) {
            keyString += delim + Crypto.encodeKey(keys[i]);
            delim = ":"; /*intentional*/
        }
        return newMessage("CHAT", keyString);
    }
    
    public static Message newPCHAT(String convoSig, String msg) {
        return newMessage("PCHAT", convoSig + ":" + msg);
    }
    
    public static Message newPOST(String msg) {
        return newMessage("POST", msg);
    }
    
    public static Message newFPOST(String msg) {
        return newMessage("FPOST", msg);
    }
    
    public static Message newCMNT(String itemSig, String comment) {
        return newMessage("CMNT", itemSig + ":" + comment);
    }
    
    public static Message newLIKE(String itemSig) {
        return newMessage("LIKE", itemSig);
    }
    
    public static Message newEVNT(long start, long end, String descrip) {
        return newMessage("EVNT", start + ":" + end + ":" + descrip);
    }
}
