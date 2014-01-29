import java.util.Vector;
import java.security.*;

class Database {
    public Database (String location) {
        System.out.println("WARNING: Dummy database constructor");
        path = location;
        posts = new Vector<Pair<String, Message>>();
        friends = new Vector<Friend>();
        
        friends.add(new Friend("me", Crypto.getPublicKey()));
    }
    
    public void close () {
    }
    
    public Vector<Pair<String, Message>> getPosts () {
        return posts;
    }
    
    public Vector<Message> getPostsBy (String name) {
        Vector<Message> msgs = new Vector<Message> ();
        for (int i = 0; i < posts.size(); i++)
            if (posts.get(i).first.equals(name))
                msgs.add(posts.get(i).second);
        return msgs;
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
        posts.add(new Pair<String, Message>(getSignatory(post), post));
    }
    
    private String path;
    Vector<Pair<String, Message>> posts; //<String author, Message m>
    private Vector<Friend> friends;
}
