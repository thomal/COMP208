//To reduce RPC calls

package ballmerpeak.turtlenet.shared;

import java.io.Serializable;

public class PostDetails implements Serializable {
    public PostDetails () {
    }
    
    public PostDetails (String _sig, boolean _liked, int _commentCount, Long _timestamp, String _posterUsername, String _text, String _posterKey) {
        sig = _sig;
        liked = _liked;
        commentCount = _commentCount;
        timestamp = _timestamp;
        posterKey = _posterKey;
        posterUsername = _posterUsername;
        text = _text;
    }
    
    public String  sig;
    public boolean liked;
    public int     commentCount;
    public Long    timestamp;
    public String  posterKey;
    public String  posterUsername;
    public String  text;
}
