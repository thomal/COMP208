package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.client.Turtlenet;
import ballmerpeak.turtlenet.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TurtlenetImpl extends RemoteServiceServlet implements Turtlenet {

  public String greetServer(String input) throws IllegalArgumentException {
    if (!FieldVerifier.isValidName(input)) {
      throw new IllegalArgumentException(
          "Name must be at least 4 characters long");
    }

    return "Ignoring: \"" + input + "\"";
  }
}
