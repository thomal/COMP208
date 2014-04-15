package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("messagefactory")
public interface MessageFactory extends RemoteService {
    public Message newMessage(String cmd, String content);
    public Message newCLAIM(String username);
    public Message newREVOKE(long time);
    public Message newPDATA(String field, String value);
    public Message newCHAT(String[] keys);
    public Message newPCHAT(String convoSig, String msg);
    public Message newPOST(String msg);
    public Message newFPOST(String msg);
    public Message newCMNT(String itemSig, String comment);
    public Message newLIKE(String itemSig);
    public Message newEVNT(long start, long end, String descrip);
}
