class GUI {
    public GUI (Database db) {
        System.out.println("WARNING: Dummy GUI constructor");
        database = db;
    }
    
    public void update () {
        System.out.println("WARNING: Dummy GUI update method");
    }
    
    Database database;
}
