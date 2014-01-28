class Database {
    public Database (String location) {
        System.out.println("WARNING: Dummy database constructor");
        path = location;
    }
    
    public void close () {
        System.out.println("WARNING: Dummy database close");
    }
    
    public void handle (String msg) {
        String message = Crypto.decrypt(msg, this);
        
        if (message != "") {
            System.out.println("WARNING: Cannot handle message \"" + msg + "\"");
        }
    }
    
    private String path;
}
