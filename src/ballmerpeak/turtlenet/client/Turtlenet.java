package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("turtlenet")
public interface Turtlenet extends RemoteService {
  String    startTN(String password);
  String    stopTN();
  Message[] demoDBCall();
}
