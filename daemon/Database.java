import java.util.Vector;
import java.security.*;

//NB: My initial version is crude, inefficient, and not even a database. It
//    exists solely to allow me to write other parts of the system. While the
//    methods are useful, their implementation should be rewritten in almost all
//    cases.

class Database {
    public Database (String location) {
        System.out.println("WARNING: Dummy database constructor");
        path    = location;
        posts   = new Vector<Pair<String, Message>>();
        claims  = new Vector<Message>();
        friends = new Vector<Friend>();
        
        addFriend(Crypto.getPublicKey());
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
    
    public void addFriend (PublicKey k) {
        friends.add(new Friend(getName(k), k));
    }
    
    public void addClaim (Message claim) {
        claims.add(claim);
        for (int i = 0; i < friends.size(); i++)
            if (friends.get(i).getName().equals("unknown"))
                friends.get(i).setName(getName(friends.get(i).getKey()));
        recalcPostAuthors();
    }
    
    private void recalcPostAuthors () {
        for (int i = 0; i < posts.size(); i++)
            posts.get(i).first = getSignatory(posts.get(i).second);
    }
    
    public String getName (PublicKey k) {
        for (int i = 0; i < claims.size(); i++)
            if (Crypto.verifySig(claims.get(i), k))
                return claims.get(i).getContent();
        return "unknown";
    }
    
    private String path; //path to database directory
    Vector<Pair<String, Message>> posts; //<String author, Message m>
    Vector<Message> claims;
    
    private Vector<Friend> friends;
}
