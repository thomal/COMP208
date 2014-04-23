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
    
    //Profile Data
    public String getMyUsername() {
        String name;
        if ((name = c.db.getName(Crypto.getPublicKey())) != null)
            return name;
        else
            return "<no username>";
    }
    
    public String getUsername(String key) {
        //TODO TurnetImpl remove this debug code
        String name;
        if ((name = c.db.getName(Crypto.decodeKey(key))) != null)
            return name;
        else if (key.equals("falsekey1"))
            return "aubri";
        else if (key.equals("falsekey2"))
            return "skandranon";
        else if (key.equals("falsekey3"))
            return "zhaneel";
        else
            return "<no username>";
    }
    
    public String getMyPDATA(String field) {
        return getPDATA(field, Crypto.encodeKey(Crypto.getPublicKey()));
    }
    
    public String getPDATA(String field, String key) {
        //TODO TurnetImpl remove this debug code
        String value;
        if ((value = c.db.getPDATA(field, Crypto.decodeKey(key))) != null)
            return value;
        else if (key.equals("falsekey1"))
            return "aubri's " + field;
        else if (key.equals("falsekey2"))
            return "skandranon's " + field;
        else if (key.equals("falsekey3"))
            return "zhaneels's " + field;
        else
            return "<no value>";
    }
    
    public String[][] getCategories () {
        String[][] categories = c.db.getCategories();
        if (categories == null) {
            categories = new String[2][2];
            categories[0][0] = "<you have no categories yet>";
            categories[0][1] = "false";
        }
        return categories;
    }
    
    public String[][] getCategoryMembers (String category) {
        PublicKey[] keys = c.db.getCategoryMembers(category);
        
        if (keys != null) {        
            String[][] pairs = new String[keys.length][2];
            for (int i = 0; i < keys.length; i++) {
                pairs[i][0] = c.db.getName(keys[i]);
                pairs[i][1] = Crypto.encodeKey(keys[i]);
            }
            return pairs;
        } else {
            //TODO TurnetImpl remove this debug code
            Logger.write("INFO", "TnImpl", "Returning false getCategoryMembers results");
            String[][] fakes = new String[3][2];
            fakes[0][0] = "aubri";
            fakes[0][1] = "<falsekey1>";
            fakes[1][0] = "skandranon";
            fakes[1][1] = "<falsekey2>";
            fakes[2][0] = "zhaneel";
            fakes[2][1] = "<falsekey3>";
            return fakes;
        }
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
}
