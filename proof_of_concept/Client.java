class Client {
    public static void main (String[] argv) {
        NetworkConnection connection = new NetworkConnection("127.0.0.1");
        Database          database   = new Database("./db");
        GUI               ui         = new GUI(database);
        
        while (running) {
            if (connection.hasMessage())
                database.handle(connection.getMessage());
            ui.update();
        }
        
        connection.close();
        database.close();
    }
    
    private static boolean running = true;
}
