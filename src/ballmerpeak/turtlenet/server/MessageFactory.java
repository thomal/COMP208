//Necceccitated as not being another Message constructor by GWT

package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.server.Crypto;

public class MessageFactory {
    public static Message newMessage(String cmd, String content) {
        long timestamp = System.currentTimeMillis();
        return new Message(cmd, content, System.currentTimeMillis(), Crypto.sign(timestamp + content));
    }
}
