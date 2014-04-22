package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
    void startTN(String password, AsyncCallback<String> callback);
    void stopTN(AsyncCallback<String> callback);
    
    void getUsername(AsyncCallback<String> callback);
    void getCategoryMembers(String category, AsyncCallback<String[][]> callback);
    
    void claimUsername(String uname, AsyncCallback<String> callback);
}
