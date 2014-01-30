import java.util.Vector;
import java.util.Date;
import java.security.*;
import java.io.*;
import java.net.*;

class NetworkConnection {
    public NetworkConnection (String serverurl) {
        url      = serverurl;
        messages = new Vector<String>();
        lastRead = 0;
        System.out.println("UNIMPLEMENTED: NetworkConnection(...) ought to" +
                           "read the current value of lastRead from file.");
    }
    
    public void close () {
        System.out.println("UNIMPLEMENTED: NetworkConnection.close() ought to" +
                           "save the current value of lastRead to file.");
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
    
    public long getTime () {
        Vector<String> time = serverCmd("t");
        
        if (time.size() == 2)
            return Long.parseLong(time.get(0));
        else
            System.out.println("ERROR: Couldn't retreive time from server");
            
        return 0;
    }
    
    public void postMessage (String msg, PublicKey recipient) {
        try {
            String ciphertext = Crypto.encrypt("POST", msg, recipient, this);
            if (!serverCmd("s " + ciphertext).get(0).equals("s"))
               throw new Exception("server reported failure");
        } catch (Exception e) {
            System.out.println("ERROR: Could not upload message: " + e);
        }
    }
    
    //The only time unencrypted data is sent
    public Boolean claimName (String name) {
        try {
            Message claim = new Message("CLAIM", name,
                     getTime()+Crypto.rand(0,50), Crypto.sign(name));
            String cmd = "c " + Crypto.Base64Encode(claim.toString().getBytes("UTF-8"));
            if (serverCmd(cmd).get(0).equals("s"))
                return true;
        } catch (Exception e) {
            System.out.println("ERROR: Could not register name: " + e);
        }
    
        return false;
    }
    
    public void downloadNewMessages () {
        Vector<String> msgs = serverCmd("get " + lastRead);
        lastRead = getTime();
        
        for (int i = 0; i < msgs.size(); i++)
            if (!(msgs.get(i) == null) && !msgs.get(i).equals("s") && !msgs.get(i).equals("e"))
                messages.add(msgs.get(i));
    }
    
    //send text to the server, recieve its response
    private Vector<String> serverCmd(String cmd) {
        Socket s;
        BufferedReader in;
        PrintWriter out;
        
        //connect
        try {
            s   = new Socket(url, port);
            in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("ERROR in connecting: " + e.getMessage());
            return null;
        }
        
        //send command
        out.println(cmd);
        out.flush();
        
        //recieve output of server
        Vector<String> output = new Vector<String>();
        try {
            String line = null;
            do {
                line = in.readLine();
                if (line != null)
                    output.add(line);
            } while (line != null);
        } catch (Exception e) {
            System.out.println("ERROR reading from server: " + e.getMessage());
        }
        
        //disconnect
        try {
            out.close();
        } catch (Exception e) {
            System.out.println("ERROR in disconnecting: " + e.getMessage());
        }
        
        try {
            in.close();
        } catch (Exception e) {
            System.out.println("ERROR in disconnecting: " + e.getMessage());
        }
        
        try {
            s.close();
        } catch (Exception e) {
            System.out.println("ERROR in disconnecting: " + e.getMessage());
        }
        
        return output;
    }
    
    private String url;
    private final int port = 31415;
    private Vector<String> messages;
    private long lastRead;
}
