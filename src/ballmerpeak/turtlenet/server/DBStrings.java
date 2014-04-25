package ballmerpeak.turtlenet.server;

class DBStrings {
    public static final String[] createDB = {
        "CREATE TABLE tConvos ("+
        "convoID     TEXT,"+
        "timeCreated TEXT,"+
        "PRIMARY KEY (convoID)"+
        ");",
        
        "CREATE TABLE tConvoKeys ("+
        "pk      INT AUTOINCREMENT,"+
        "convoID INT,"+
        "key     TEXT,"+
        "PRIMARY KEY (pk));",
        
        "CREATE TABLE tConvoMessages ("+
        "pk         INT AUTOINCREMENT,"+
        "convoID    INT,"+
        "sendersKey TEXT,"+
        "msgText    TEXT,"+
        "time       TEXT,"+
        "PRIMARY KEY (pk));",
        
        "CREATE TABLE tPost ("+
        "postID      INT AUTOINCREMENT,"+
        "msgText     TEXT,"+
        "time        TEXT,"+
        "sig         TEXT,"+
        "recieverKey TEXT,"+ //person whos wall it was posted on
        "sendersKey  TEXT,"+
        "PRIMARY KEY (postID));",
        
        "CREATE TABLE tPostVisibleTo ("+
        "pk     INT AUTOINCREMENT,"+
        "postID INT,"+
        "key    TEXT,"+
        "PRIMARY KEY (pk));",
        
        "CREATE TABLE tUser ("+
        "key      TEXT,"+
        "username TEXT,"+
        "knowName INT,"+                     //1 if we know the username for this key, 0 otherwise
        "email    TEXT,"+
        "name     TEXT,"+
        "gender   TEXT,"+
        "birthday TEXT,"+
        "PRIMARY KEY (key));",
        
        "CREATE TABLE tCategory ("+
        "catID       TEXT,"+
        "canSeePDATA INT,"+                  //1 if category can see pdata, 0 otherwise
        "PRIMARY KEY (catID));",
        
        "CREATE TABLE tCategoryMembers ("+
        "pk      INT AUTOINCREMENT,"+
        "catID   TEXT,"+
        "userKey TEXT,"+
        "PRIMARY KEY (pk));",
        
        "CREATE TABLE tEvent ("+
        "eventID      INT AUTOINCREMENT,"+
        "startTime    TEXT,"+
        "endTime      TEXT,"+
        "creatorKey   TEXT,"+
        "accepted     INT,"+                   //1 if category can see pdata, 0 otherwise
        "sig          TEXT,"+
        "name         TEXT,"+
        "creationTime TEXT,"+
        "PRIMARY KEY (eventID));",
        
        "CREATE TABLE tClaim ("+
        "claimID   INT AUTOINCREMENT,"+
        "sig       TEXT,"+
        "name      TEXT,"+
        "claimTime TEXT,"+
        "PRIMARY KEY (claimID));",
        
        "CREATE TABLE tLike ("+
        "pk       INT AUTOINCREMENT,"+
        "likerKey TEXT,"+
        "parent   TEXT,"+                     //sig of thing being liked
        "PRIMARY KEY (pk));",
        
        "CREATE TABLE tComment ("+
        "sig          TEXT,"+
        "text         TEXT,"+
        "parent       TEXT,"+                 //sig of thing being commented
        "commenterKey TEXT,"+
        "creationTime TEXT,"+
        "PRIMARY KEY (sig));",
    };
    
    public static final String getPDATA         = "SELECT '__FIELD__' FROM tUser WHERE key = '__KEY__';";
    public static final String getWallPostIDs   = "SELECT postID FROM tPost WHERE reciverKey = '__KEY__';";
    public static final String getPost          = "SELECT time, sig, msgText, recieversKey FROM tPost WHERE postID = '__ID__';";
    public static final String getVisibleTo     = "SELECT key FROM tPostVisibleTo WHERE postID = '__ID__';";
    public static final String getConversation  = "SELECT senderKey, msgText, time FROM tConvoMessages WHERE convoID = '__SIG__';";
    public static final String getConversations = "SELECT * FROM tConvos;";
    public static final String getConversationMembers  = "SELECT key FROM tConvoKeys WHERE sig = __SIG__;";
    public static final String getConversationMessages = "SELECT sendersKey, time, msgText FROM tConvoMessages WHERE sig = '__SIG__';";
    public static final String getKey           = "SELECT key FROM tUser WHERE username = '__USERNAME__';";
    public static final String getCategories    = "SELECT * FROM tCategory;";
    public static final String getCategory      = "SELECT * FROM tCategory WHERE catID = '__CATNAME__';";
    public static final String getAllKeys       = "SELECT key FROM tUser;";
    public static final String getMemberKeys    = "SELECT userKey FROM tCategoryMembers WHERE catID = '__CATNAME__';";
    public static final String getName          = "SELECT username FROM tUser WHERE key = '__KEY__';";
}
