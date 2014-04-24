package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Conversation;
import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
    void startTN(String password, AsyncCallback<String> callback);
    void stopTN(AsyncCallback<String> callback);
    
    //Profile Data
    void getUsername   (String key,              AsyncCallback<String> callback);
    void getMyUsername                          (AsyncCallback<String> callback);
    void getPDATA      (String field, String pk, AsyncCallback<String> callback);
    void getMyPDATA    (String pk,               AsyncCallback<String> callback);
    void getKey        (String username,         AsyncCallback<String> callback);
    void getMyKey                               (AsyncCallback<String> callback);
    
    void getPeople                           (AsyncCallback<String[][]> callback);
    void getCategories                       (AsyncCallback<String[][]> callback);
    void getCategoryMembers (String category, AsyncCallback<String[][]> callback);
    
    void getConversation         (String sig, AsyncCallback<Conversation> callback);
    void getConversations                    (AsyncCallback<Conversation[]> callback);
    void getConversationMessages (String sig, AsyncCallback<String[][]> callback);
    
    //Profile Data
    void claimUsername         (String uname,                   AsyncCallback<String> callback);
    void updatePDATA           (String field, String value,     AsyncCallback<String> callback);
    void updatePDATApermission (String category, boolean value, AsyncCallback<String> callback);
    
    //Posting
    void createCHAT       (String[] keys,           AsyncCallback<String[]> callback);
    void addMessageToCHAT (String text, String sig, AsyncCallback<String> callback);
    
    //Friends
    void addCategory   (String name,             AsyncCallback<String> callback);
    void addToCategory (String name, String key, AsyncCallback<String> callback);
    void addKey        (String key,              AsyncCallback<String> callback);
}
