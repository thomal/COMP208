package ballmerpeak.turtlenet.server;

import java.security.PublicKey;

public class Friend {
    public Friend (String _name, PublicKey _key) {
        name = _name;
        key = _key;
    }
    
    public String getName () {
        return name;
    }
    
    public void setName (String nname) {
        name = nname;
    }
    
    public PublicKey getKey () {
        return key;
    }
    
    private String name;
    private PublicKey key;
}
