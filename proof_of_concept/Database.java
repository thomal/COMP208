import java.util.Vector;
import java.security.*;

class Database {
    public Database (String location) {
        System.out.println("WARNING: Dummy database constructor");
        path = location;
        posts = new Vector<Message>();
        friends = new Vector<Friend>();
        
        friends.add(new Friend("me", Crypto.getPublicKey()));
    }
    
    public void close () {
    }
    
    public Vector<Message> getPosts () {
        return posts;
    }
    
    public Vector<Friend> getFriends () {
        return friends;
    }
    
    public PublicKey getKey (String name) {
        for (int i = 0; i < friends.size(); i++)
            if (friends.get(i).getName().equals(name))
                return friends.get(i).getKey();
        
        return null;
    }
    
    public String getSignatory (Message m) {
        for (int i = 0; i < friends.size(); i++) {
            if (Crypto.verifySig(m, friends.get(i).getKey()))
                return friends.get(i).getName();
        }
        return "unknown";
    }
    
    public void addPost (Message post) {
        posts.add(post);
    }
    
    private String path;
    private Vector<Message> posts;
    private Vector<Friend> friends;
}
