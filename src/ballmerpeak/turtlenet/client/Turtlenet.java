package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.CommentDetails;
import ballmerpeak.turtlenet.shared.PostDetails;
import ballmerpeak.turtlenet.shared.Conversation;
import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("turtlenet")
public interface Turtlenet extends RemoteService {
  String           startTN                 (String password); //!< Start turtlenet client on the backend.
  String           stopTN                  ();                //!< Stop turtlenet client on the backend.
  /** Check if this is the first time Turtlenet has been run.
   * \return "true" if this is the first time turtlenet has been run, "false" otherwise.
   */
  String           isFirstTime             ();
  String           register                (String username, String password);
  
  String           getUsername             (String key);
  String           getMyUsername           ();
  String           getPDATA                (String field, String key);
  String           getMyPDATA              (String field);
  String           getKey                  (String username);
  String           getMyKey                ();
  String[][]       getPeople               ();                //{{"name1","key1"}, {"name2","key2"}}
  String[][]       getCategories           ();                //{{"friends", "false"}, {"family", "true"}}
  String[][]       getCategoryMembers      (String category); //{{"name1","key1"}, {"name2","key2"}}
  Conversation     getConversation         (String sig);
  Conversation[]   getConversations        ();
  String[][]       getConversationMessages (String sig);
  PostDetails[]    getWallPosts            (String key);
  CommentDetails[] getComments             (String parent);
  Long             timeMostRecentWallPost  (String key);
  Long             getConvoLastUpdated     (String sig);
  Long             getPostLastCommented    (String sig);
  
  String           claimUsername           (String uname);
  String           updatePDATA             (String field, String newValue);
  String           updatePDATApermission   (String category, boolean value);
  String[]         createCHAT              (String[] keys); //{"success", "<convo signature>"}
  String           addMessageToCHAT        (String text, String sig);
  String           like                    (String sig);
  String           unlike                  (String sig);
  String           addCategory             (String name);
  String           addToCategory           (String category, String key);
  String           addKey                  (String key);
  String           addPost                 (String wallKey, String categoryVisibleTo, String msg);
  String           addComment              (String parent, String text);
  String           removeFromCategory      (String group, String key);
  String           revokeMyKey             ();
}
