class Client {
    public static void main (String[] argv) {
        NetworkConnection connection    = new NetworkConnection("127.0.0.1");
        Thread            networkThread = new Thread(connection);
        Database          db            = new Database("./db");
        GUI               gui           = new GUI(db, connection);
        
        if (!Crypto.keysExist())
            Crypto.keyGen();
        
        networkThread.start();
        
        while (running) {
            while (connection.hasMessage())
                Parser.handle(Crypto.decrypt(connection.getMessage()), db);
            running = gui.update();
        }
        
        gui.close();
        connection.close();
        db.close();
    }
    
    private static boolean running = true;
}
