package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import ballmerpeak.turtlenet.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.*;

@SuppressWarnings("serial")
public class TurtlenetImpl extends RemoteServiceServlet implements Turtlenet {

    public String test(String input) throws IllegalArgumentException {
        if (!FieldVerifier.isValidName(input))
            throw new IllegalArgumentException("Command must be at least 4 characters long");

        try {
            File lastReadFile = new File("./db/lastread");
                        
            BufferedWriter writer = new BufferedWriter(new FileWriter("./TESTFILE"));
            writer.write(input + "\n");
            writer.close();
            return "Success";
        } catch (Exception e) {
            return "ERROR: Unable to save file: " + e.toString();
        }
    }
}
