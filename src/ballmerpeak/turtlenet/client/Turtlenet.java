package ballmerpeak.turtlenet.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("turtlenet")
public interface Turtlenet extends RemoteService {
  String test(String name) throws IllegalArgumentException;
  String startTN();
  String stopTN();
}
