package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.*;
import java.security.*;
import ballmerpeak.turtlenet.server.TNClient;
import ballmerpeak.turtlenet.server.MessageFactoryImpl;
import ballmerpeak.turtlenet.shared.Message;

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
    
    public String[][] getCategoryMembers (String category) {
        PublicKey[] keys = c.db.getCategoryMembers(category);
        String[][] pairs = new String[keys.length][2];
        for (int i = 0; i < keys.length; i++) {
            pairs[i][0] = c.db.getName(keys[i]);
            pairs[i][1] = Crypto.encodeKey(keys[i]);
        }
        return pairs;
    }
}
