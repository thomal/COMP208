package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.CommentDetails;
import ballmerpeak.turtlenet.shared.PostDetails;
import ballmerpeak.turtlenet.shared.Conversation;
import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TurtlenetAsync {
    void startTN                 (String password,                                  AsyncCallback<String> callback);
    void stopTN                  (                                                  AsyncCallback<String> callback);
    void isFirstTime             (                                                  AsyncCallback<String> callback);
    void register                (String username, String password,                 AsyncCallback<String> callback);
    
    void getUsername             (String key,                                       AsyncCallback<String> callback);
    void getMyUsername           (                                                  AsyncCallback<String> callback);
    void getPDATA                (String field, String pk,                          AsyncCallback<String> callback);
    void getMyPDATA              (String pk,                                        AsyncCallback<String> callback);
    void getKey                  (String username,                                  AsyncCallback<String> callback);
    void getMyKey                (                                                  AsyncCallback<String> callback);
    void getPeople               (                                                  AsyncCallback<String[][]> callback);
    void getCategories           (                                                  AsyncCallback<String[][]> callback);
    void getCategoryMembers      (String category,                                  AsyncCallback<String[][]> callback);
    void getConversation         (String sig,                                       AsyncCallback<Conversation> callback);
    void getConversations        (                                                  AsyncCallback<Conversation[]> callback);
    void getConversationMessages (String sig,                                       AsyncCallback<String[][]> callback);
    void getWallPosts            (String key,                                       AsyncCallback<PostDetails[]> callback);
    void getComments             (String parent,                                    AsyncCallback<CommentDetails[]> callback);
    void timeMostRecentWallPost  (String key,                                       AsyncCallback<Long> callback);
    void getConvoLastUpdated     (String sig,                                       AsyncCallback<Long> callback);
    void getPostLastCommented    (String sig,                                       AsyncCallback<Long> callback);
    
    void claimUsername           (String uname,                                     AsyncCallback<String> callback);
    void updatePDATA             (String field, String value,                       AsyncCallback<String> callback);
    void updatePDATApermission   (String category, boolean value,                   AsyncCallback<String> callback);
    void createCHAT              (String[] keys,                                    AsyncCallback<String[]> callback);
    void addMessageToCHAT        (String text, String sig,                          AsyncCallback<String> callback);
    void like                    (String sig,                                       AsyncCallback<String> callback);
    void unlike                  (String sig,                                       AsyncCallback<String> callback);
    void addCategory             (String name,                                      AsyncCallback<String> callback);
    void addToCategory           (String name, String key,                          AsyncCallback<String> callback);
    void addKey                  (String key,                                       AsyncCallback<String> callback);
    void addPost                 (String key, String categoryVisibleTo, String msg, AsyncCallback<String> callback);
    void addComment              (String parent, String text,                       AsyncCallback<String> callback);
    void removeFromCategory      (String group, String key,                         AsyncCallback<String> callback);
    void revokeMyKey             (                                                  AsyncCallback<String> callback);
}
