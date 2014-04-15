package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageFactoryAsync {
    void newMessage(String cmd, String content,           AsyncCallback<Message> callback);
    void newCLAIM  (String username,                      AsyncCallback<Message> callback);
    void newREVOKE (long time,                            AsyncCallback<Message> callback);
    void newPDATA  (String field, String value,           AsyncCallback<Message> callback);
    void newCHAT   (String[] keys,                        AsyncCallback<Message> callback);
    void newPCHAT  (String convoSig, String msg,          AsyncCallback<Message> callback);
    void newPOST   (String msg,                           AsyncCallback<Message> callback);
    void newFPOST  (String msg,                           AsyncCallback<Message> callback);
    void newCMNT   (String itemSig, String comment,       AsyncCallback<Message> callback);
    void newLIKE   (String itemSig,                       AsyncCallback<Message> callback);
    void newEVNT   (long start, long end, String descrip, AsyncCallback<Message> callback);
}
