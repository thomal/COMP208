package ballmerpeak.turtlenet.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface Turtlenet extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException;
}
