package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.*;
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
        TNClient.running = false;
        return "success";
    }
    
    /*Use this as a template for further functions. Remember to add methods to
      the TurtlenetAsync.java and Turtlenet.java interfaces. When making a post
      use c.connection.postMessage(msg, key)*/
    public Message[] demoDBCall () {
        return c.db.getPostsBy(c.db.getKey("john_doe")[0]); /*get all posts made with the first public key using the name "john_doe" (there _should_ only be one, if multiple are returned user should be asked for clarification based on DoB or something)*/
    }
}
