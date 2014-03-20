package ballmerpeak.turtlenet.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
  void test(String input, AsyncCallback<String> callback)
      throws IllegalArgumentException;
}
