import java.util.Vector;
import java.security.*;

class NetworkConnection {
    public NetworkConnection (String serverurl) {
        System.out.println("WARNING: Duumy network connection constructor");
        url = serverurl;
        messages = new Vector<String>();
    }
    
    public void close () {
    }
    
    public Boolean hasMessage () {
        return messages.size() >= 1;
    }
    
    public String getMessage() {
        String m = messages.get(0);
        messages.removeElementAt(0);
        return m;
    }
    
    public void postMessage (String msg, PublicKey recipient) {
        System.out.println("WARNING: Dummy post method");
        messages.add(Crypto.encrypt("POST", msg, recipient));
    }
    
    private String url;
    private Vector<String> messages;
}
