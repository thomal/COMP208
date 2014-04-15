package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
  void startTN(AsyncCallback<String> callback);
  void stopTN(AsyncCallback<String> callback);
  void demoDBCall(AsyncCallback<String> callback);
}
