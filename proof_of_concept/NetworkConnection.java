import java.util.Vector;
import java.util.*; //time
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
        
        try {
        Date now = new Date();
        String ciphertext = Crypto.encrypt("POST", msg, recipient);
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
        try {
            File server = new File("./fakeserver");
            File[] files = server.listFiles();
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = new BufferedReader(
                                        new FileReader(files[i]));
                String msg = reader.readLine();
                if (lastRead <= getTimestamp(files[i])) {
                    messages.add(msg);
                }
            }
            
            lastRead = new Date().getTime();
        } catch (Exception e) {
            System.out.println("ERROR: Could not read from server: " + e);
        }
    }
    
    //44634633434_HASH -> 44634633434
    private double getTimestamp (File f) {
        try {
            String fn = f.getCanonicalPath();
            String ts = fn.substring(fn.lastIndexOf("/")+1, fn.lastIndexOf("_"));
            
            return Double.parseDouble(ts);
        } catch (Exception e) {
            System.out.println("ERROR: Could not parse file timestamp: " + e);
        }
        return 1;
    }
    
    private String url;
    private Vector<String> messages;
    private long lastRead;
}
