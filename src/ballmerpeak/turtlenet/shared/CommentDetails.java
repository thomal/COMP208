//To reduce RPC calls

package ballmerpeak.turtlenet.shared;

import java.io.Serializable;

public class CommentDetails implements Serializable {
    public CommentDetails () {
    }
    
    public CommentDetails (String _posterKey, String _posterName, String _sig, String _text, boolean _liked) {
        posterKey = _posterKey;
        posterName = _posterName;
        sig = _sig;
        text = _text;
        liked = _liked;
    }
    
    public String posterKey;
    public String posterName;
    public String sig;
    public String text;
    public boolean liked;
}
