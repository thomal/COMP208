import java.util.Vector;
import java.security.*;

class Database {
    public Database (String location) {
        System.out.println("WARNING: Dummy database constructor");
        path = location;
        posts = new Vector<String>();
        friends = new Vector<Friend>();
        
        friends.add(new Friend("me", Crypto.getPublicKey()));
    }
    
    public void close () {
    }
    
    public Vector<String> getPosts () {
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
    
    public void addPost (String post) {
        posts.add(post);
    }
    
    private String path;
    private Vector<String> posts;
    private Vector<Friend> friends;
}
