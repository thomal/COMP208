//Can not be Message constructors because of GWT
//These methods can't be static like they should be because of GWT

package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.MessageFactory;
import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.server.Crypto;
import java.security.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MessageFactoryImpl extends RemoteServiceServlet implements MessageFactory {
    public MessageFactoryImpl(){
    }

    public  Message newMessage(String cmd, String content) {
        long timestamp = System.currentTimeMillis();
        return new Message(cmd, content, System.currentTimeMillis(), Crypto.sign(timestamp + content));
    }
    
    public Message newCLAIM(String username) {
        return newMessage("CLAIM", username);
    }
    
    public Message newREVOKE(long time) {
        return newMessage("REVKOE", ""+time);
    }
    
    public Message newPDATA(String field, String value) {
        return newMessage("PDATA", field + ":" + value + ";");
    }
    
    public Message newCHAT(PublicKey[] keys) {
        String keyString = "";
        String delim = "";
        for (int i = 0; i < keys.length; i++) {
            keyString += delim + Crypto.encodeKey(keys[i]);
            delim = ":"; /*intentional*/
        }
        return newMessage("CHAT", keyString);
    }
    
    public Message newCHAT(String[] keys) {
        String keyString = "";
        String delim = "";
        for (int i = 0; i < keys.length; i++) {
            keyString += delim + keys[i];
            delim = ":"; /*intentional*/
        }
        return newMessage("CHAT", keyString);
    }
    
    public Message newPCHAT(String convoSig, String msg) {
        return newMessage("PCHAT", convoSig + ":" + msg);
    }
    
    public Message newPOST(String msg) {
        return newMessage("POST", msg);
    }
    
    public Message newFPOST(String msg) {
        return newMessage("FPOST", msg);
    }
    
    public Message newCMNT(String itemSig, String comment) {
        return newMessage("CMNT", itemSig + ":" + comment);
    }
    
    public Message newLIKE(String itemSig) {
        return newMessage("LIKE", itemSig);
    }
    
    public Message newEVNT(long start, long end, String descrip) {
        return newMessage("EVNT", start + ":" + end + ":" + descrip);
    }
}
