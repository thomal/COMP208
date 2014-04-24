package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Conversation;
import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("turtlenet")
public interface Turtlenet extends RemoteService {
  String     startTN(String password);
  String     stopTN();
  
  //Profile Data
  String     getUsername(String key);
  String     getMyUsername();
  String     getPDATA(String field, String key);
  String     getMyPDATA(String field);
  String     getKey(String username);
  String     getMyKey();
  
  String[][] getPeople();                         //{{"name1","key1"}, {"name2","key2"}}
  String[][] getCategories();                     //{{"friends", "false"}, {"family", "true"}}
  String[][] getCategoryMembers(String category); //{{"name1","key1"}, {"name2","key2"}}
  
  Conversation   getConversation(String sig);
  Conversation[] getConversations();
  String[][]     getConversationMessages(String sig);
  
  //Profile Data
  String     claimUsername(String uname);
  String     updatePDATA(String field, String newValue);
  String     updatePDATApermission(String category, boolean value);
  
  //Posting
  String[]   createCHAT(String[] keys); //{"success", "<convo signature>"}
  String     addMessageToCHAT(String text, String sig);
  
  //Friends
  String addCategory   (String name);
  String addToCategory (String category, String key);
}
