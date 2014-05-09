package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import java.util.Vector;
import java.util.Date;
import java.security.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;

public class NetworkConnection implements Runnable {
    /** Construct a network connection that connects to the given URL.
     * \param serverurl The URl to connect to.
     */
    public NetworkConnection (String serverurl) {
        url         = serverurl;
        messages    = new Vector<String>();
        lastRead    = 0;
        messageLock = new Semaphore(1);
        connected   = true;
        tor         = true;
        
        //parse db/lastread
        File lastReadFile = new File("./db/lastread");
        if (lastReadFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(
                                            new FileReader(lastReadFile));
                lastRead = Long.parseLong(reader.readLine());
                Logger.write("INFO", "NetCon","Read lastRead from file");
            } catch (Exception e) {
                Logger.write("ERROR", "NetCon", "Could not read lastread from file");
            }
        }
    }
    
    /** Used for constructing a network thread.
     * Fetches new messages every second. Thread safe.
     */
    public void run () {
        Logger.write("INFO", "NetCon","NetworkConnection started");
        while (connected) {
            try {
                Thread.sleep(1000); //update every second
            } catch (Exception e) {
                Logger.write("WARNING", "NetCon", "Sleep interrupted: " + e);
            }
            downloadNewMessages();
        }
    }
    
    /** Shutdown the network connection.
     * Saves the last time at which new messages were fetched to disk.
     * Thread safe.
     */
    public void close () {
        Logger.write("INFO", "NetCon","close()");
        connected = false;
        try {
            File lastReadFile = new File("./db/lastread");
            
            if (lastReadFile.exists())
                lastReadFile.delete();
            
            BufferedWriter writer = new BufferedWriter(
                                    new FileWriter(lastReadFile));
            writer.write(Long.toString(lastRead));
            writer.close();
            Logger.write("INFO", "NetCon","Saved lastRead to disk");
        } catch (Exception e) {
            Logger.write("ERROR", "NetCon", "Unable to save lastRead: " + e);
        }
    }
    
    /** Checks for new messages.
     * Thread safe.
     * \return returns true if a message is available, false otherwise
     */
    public Boolean hasMessage () {
        try {
            messageLock.acquire();
            boolean haveMessage = messages.size() >= 1;
            messageLock.release();
            return haveMessage;
        } catch (Exception e) {
            Logger.write("WARNING", "NetCon", "Acquire interrupted");
        }
        return false;
    }
    
    /** Get a message.
     * Get the next message in the queue, and remove it from the queue.
     * Thread safe.
     * \return The oldest message in the stack of messages recieved.
     */
    public String getMessage() {
        try {
            messageLock.acquire();
            String m = messages.get(0);
            messages.removeElementAt(0);
            messageLock.release();
            return m;
        } catch (Exception e) {
            Logger.write("WARNING", "NetCon", "Acquire interrupted");
        }
        return new Message("NULL", "", 0, "").toString();
    }
    
    /** Get the server time.
     * Millisecond timestamps can, and have, been used to identify users. We
     * therefore recomend always using server time. (Obviously other methods
     * are in place to hide true network latency.)
     * Thread safe.
     * \return The number of milliseconds since midnight january first 1970,
     *  according to the server.
     */
    public long getTime () {
        Vector<String> time = serverCmd("t");
        
        if (time.size() == 2)
            return Long.parseLong(time.get(0));
        else
            Logger.write("ERROR", "NetCon", "Couldn't retreive time from server");
            
        return 0;
    }
    
    /** Post a Message object over the network.
     * Only viewable by recipitent.
     * Thread safe.
     * \param recipient The person you are sending the message to.
     * \return returns true on success, false otherwise.
     */
    public boolean postMessage (Message msg, PublicKey recipient) {
            String ciphertext = Crypto.encrypt(msg, recipient, this);
            if (!serverCmd("s " + ciphertext).get(0).equals("s")) {
                Logger.write("RED", "NetCon", "server reported failure uploading message");
                return false;
            } else {
                Logger.write("INFO", "NetCon", "uploaded message: \"" + msg + "\"");
                return true;
            }
    }
    
    /** Claims a username on the network.
     * Usernames must be unique, this is enforced by the server.
     * Thread safe.
     * \warning Extant usernames are considered public information. This is the
     * only plaintext sent in the system.
     * \param name The name to claim.
     * \return true if succes, false otherwsie.
     */
    public Boolean claimName (String name) {
        try {
            Message claim = new Message("CLAIM", name,
                     getTime()+Crypto.rand(0,50), "");
            claim.signature = Crypto.sign(claim);
            String cmd = "c " + Crypto.Base64Encode(claim.toString().getBytes("UTF-8"));
            if (serverCmd(cmd).get(0).equals("s")) {
                Logger.write("INFO", "NetCon","Claimed name: " + name);
                Logger.write("INFO", "NetCon","\tname: " + claim.CLAIMgetName());
                Logger.write("INFO", "NetCon","\ttime: " + Long.toString(claim.getTimestamp()));
                Logger.write("INFO", "NetCon","\t sig: " + claim.getSig());
                return true;
            }
        } catch (Exception e) {
            Logger.write("ERROR", "NetCon", "Could not register name: " + e);
        }
    
        Logger.write("INFO", "NetCon","Could not register name: " + name);
        return false;
    }
    
    /** Download new messages.
     * Automatically called every second by run().
     * Thread safe.
     */
    public void downloadNewMessages () {
        Vector<String> msgs = serverCmd("get " + lastRead);
        lastRead = getTime();
        
        for (int i = 0; i < msgs.size(); i++) {
            if (!(msgs.get(i) == null) && !msgs.get(i).equals("s") && !msgs.get(i).equals("e")) {
                try {
                    messageLock.acquire();
                    messages.add(msgs.get(i));
                    messageLock.release();
                } catch (Exception e) {
                    Logger.write("WARNING", "NetCon", "Acquire interrupted.");
                }
            }
        }
    }
    
    /** Send a command to the server.
     * \param cmd The text to send to the server.
     * \return The servers response, one string per line, element 0 is the
     * topmost (first) line sent by the server.
     */
    private Vector<String> serverCmd(String cmd) {
        Socket s;
        BufferedReader in;
        PrintWriter out;
        //if (!cmd.equals("t") && !cmd.substring(0,4).equals("get "))
        //    Logger.write("VERBOSE", "NetCon", "Sending command to server \""  + cmd + "\"");
        
        //connect
        try {
            if (tor) {
                s = new Socket(new Proxy(Proxy.Type.SOCKS,
                                         new InetSocketAddress("localhost", 9050))); //connect to Tor SOCKS proxy
                s.connect(new InetSocketAddress(url, port));                         //connect to server through Tor
            } else {
                s = new Socket(url, port);
            }
            
            in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (Exception e) {
            Logger.write("ERROR", "NetCon", "Could not connect to network: " + e);
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
            Logger.write("ERROR", "NetCon", "Could not read from rserver: " + e.getMessage());
        }
        
        //disconnect
        try {
            out.close();
        } catch (Exception e) {
            Logger.write("ERROR", "NetCon", "Could not disconnect from rserver: " + e.getMessage());
        }
        
        try {
            in.close();
        } catch (Exception e) {
            Logger.write("ERROR", "NetCon", "Could not disconnect from rserver: " + e.getMessage());
        }
        
        try {
            s.close();
        } catch (Exception e) {
            Logger.write("ERROR", "NetCon", "Could not close socket: " + e.getMessage());
        }
        
        return output;
    }
    
    private String url;
    private final int port = 31415; //!< Pi is awesome
    private Vector<String> messages;
    
    private long lastRead;
    private boolean connected;
    private boolean tor;
    private Semaphore messageLock;
}
