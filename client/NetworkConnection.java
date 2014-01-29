import java.util.Vector;
import java.util.Date;
import java.security.*;
import java.io.*;

class NetworkConnection {
    public NetworkConnection (String serverurl) {
        System.out.println("WARNING: Duumy network connection constructor");
        url = serverurl;
        messages = new Vector<String>();
        lastRead = -1;
    }
    
    public void close () {
    }
    
    //returns true if a message is available
    public Boolean hasMessage () {
        return messages.size() >= 1;
    }
    
    //get the next message in the queue, and remove it from the queue
    public String getMessage() {
        String m = messages.get(0);
        messages.removeElementAt(0);
        return m;
    }
    
    //gets the server time, adds between 0 and 50 milliseconds to it randomly
    //  to prevent location being discovered via timing attack
    public long getTime () {
        System.out.println("WARNING: Unimplemented getTime()");
        return 0;
    }
    
    public void postMessage (String msg, PublicKey recipient) {
        System.out.println("WARNING: Dummy post method");
        
        try {
        Date now = new Date();
        String ciphertext = Crypto.encrypt("POST", msg, recipient, this);
        BufferedWriter writer = new BufferedWriter(
                                new FileWriter(
                                new File("./fakeserver/" + now.getTime() +
                                         "_" + Crypto.hash(ciphertext))));
        writer.write(ciphertext);
        writer.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not write file");
        }
    }
    
    public void downloadMessages () {
        System.out.println("WARNING: Unimplemented downloadMessages function");
    }
    
    private String url;
    private Vector<String> messages;
    private long lastRead;
}
