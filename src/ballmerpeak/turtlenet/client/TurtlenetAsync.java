package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
    void startTN(String password, AsyncCallback<String> callback);
    void stopTN(AsyncCallback<String> callback);
    
    //Profile Data
    void getUsername (String key, AsyncCallback<String> callback);
    void getMyUsername (AsyncCallback<String> callback);
    void getPDATA    (String field, String pk, AsyncCallback<String> callback);
    void getMyPDATA  (String pk, AsyncCallback<String> callback);
    
    void getCategoryMembers(String category, AsyncCallback<String[][]> callback);
    
    //Profile Data
    void claimUsername(String uname, AsyncCallback<String> callback);
    void updatePDATA(String field, String value, AsyncCallback<String> callback);
}
