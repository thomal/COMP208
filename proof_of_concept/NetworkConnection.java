import java.util.Vector;
import java.security.*;
import java.io.*;

class NetworkConnection {
    public NetworkConnection (String serverurl) {
        System.out.println("WARNING: Duumy network connection constructor");
        url = serverurl;
        messages = new Vector<String>();
        readMessages = new Vector<String>();
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
        String ciphertext = Crypto.encrypt("POST", msg, recipient);
        BufferedWriter writer = new BufferedWriter(
                                new FileWriter(
                                new File("./fakeserver/" + Crypto.hash(ciphertext))));
        writer.write(ciphertext);
        writer.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not write file");
        }
    }
    
    public void downloadMessages () {
        System.out.println("download");
        try {
            File server = new File("./fakeserver");
            File[] files = server.listFiles();
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = new BufferedReader(
                                        new FileReader(files[i]));
                String msg = reader.readLine();
                if (!readMessages.contains(msg)) {
                    messages.add(msg);
                    readMessages.add(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: Could not read from server: " + e);
        }
    }
    
    private String url;
    private Vector<String> messages;
    private Vector<String> readMessages;
}
