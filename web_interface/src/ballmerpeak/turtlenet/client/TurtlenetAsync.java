package ballmerpeak.turtlenet.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
  void greetServer(String input, AsyncCallback<String> callback)
      throws IllegalArgumentException;
}
