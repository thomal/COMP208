package ballmerpeak.turtlenet.server;

class DBStrings {
    public static final String[] createDB = {
        "CREATE TABLE tConvos ("+
        "convoID     TEXT,"+                 //sig
        "timeCreated TEXT,"+
        "PRIMARY KEY (convoID)"+
        ");",
        
        "CREATE TABLE tConvoKeys ("+
        "pk      INTEGER PRIMARY KEY AUTOINCREMENT,"+
        "convoID TEXT,"+
        "key     TEXT"+
        ");",
        
        "CREATE TABLE tConvoMessages ("+
        "pk         INTEGER PRIMARY KEY AUTOINCREMENT,"+
        "convoID    TEXT,"+
        "sendersKey TEXT,"+
        "msgText    TEXT,"+
        "time       TEXT"+
        ");",
        
        "CREATE TABLE tPost ("+
        "sig         TEXT,"+
        "msgText     TEXT,"+
        "time        TEXT,"+
        "recieverKey TEXT,"+                 //person whos wall it was posted on
        "sendersKey  TEXT,"+
        "PRIMARY KEY (sig));",
        
        "CREATE TABLE tPostVisibleTo ("+
        "pk      INTEGER PRIMARY KEY AUTOINCREMENT,"+
        "postSig TEXT,"+
        "key     TEXT"+
        ");",
        
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
        "catID       TEXT,"+                 //sig
        "canSeePDATA INT,"+                  //1 if category can see pdata, 0 otherwise
        "PRIMARY KEY (catID));",
        
        "CREATE TABLE tCategoryMembers ("+
        "pk      INTEGER PRIMARY KEY AUTOINCREMENT,"+
        "catID   TEXT,"+
        "userKey TEXT"+
        ");",
        
        "CREATE TABLE tEvent ("+
        "sig          TEXT,"+
        "startTime    TEXT,"+
        "endTime      TEXT,"+
        "creatorKey   TEXT,"+
        "accepted     INT,"+                 //1 if category can see pdata, 0 otherwise
        "name         TEXT,"+
        "creationTime TEXT,"+
        "PRIMARY KEY (sig));",
        
        "CREATE TABLE tClaim ("+
        "sig       TEXT,"+
        "name      TEXT,"+
        "claimTime TEXT,"+
        "PRIMARY KEY (sig));",
        
        "CREATE TABLE tLike ("+
        "pk       INTEGER PRIMARY KEY AUTOINCREMENT,"+
        "likerKey TEXT,"+
        "parent   TEXT"+                     //sig of thing being liked
        ");",
        
        "CREATE TABLE tComment ("+
        "sig          TEXT,"+
        "msgText      TEXT,"+
        "parent       TEXT,"+                //sig of thing being commented
        "creationTime TEXT,"+
        "PRIMARY KEY (sig));",
        
        "CREATE TABLE tRevocations ("+
        "sig          TEXT,"+
        "timeOfLeak   TEXT,"+
        "creationTime TEXT,"+
        "PRIMARY KEY (sig));",
    };
    
    public static final String getPDATA         = "SELECT __FIELD__ FROM tUser WHERE key = '__KEY__';";
    public static final String getWallPostSigs  = "SELECT sig FROM tPost WHERE recieverKey = '__KEY__';";
    public static final String getPost          = "SELECT time, sig, msgText, recieverKey, sendersKey FROM tPost WHERE sig = '__SIG__';";
    public static final String getPostSender    = "SELECT sendersKey FROM tPost WHERE sig = '__SIG__';";
    public static final String getVisibleTo     = "SELECT key FROM tPostVisibleTo WHERE postSig = '__SIG__';";
    public static final String getConversation  = "SELECT sendersKey, msgText, time FROM tConvoMessages WHERE convoID = '__SIG__';";
    public static final String getConversations = "SELECT * FROM tConvos;";
    public static final String getConversationMembers  = "SELECT key FROM tConvoKeys WHERE convoID = '__SIG__';";
    public static final String getConversationMessages = "SELECT sendersKey, time, msgText FROM tConvoMessages WHERE convoID = '__SIG__';";
    public static final String getKey           = "SELECT key FROM tUser WHERE username = '__USERNAME__';";
    public static final String getCategories    = "SELECT * FROM tCategory;";
    public static final String getCategory      = "SELECT * FROM tCategory WHERE catID = '__CATNAME__';";
    public static final String getAllKeys       = "SELECT key FROM tUser;";
    public static final String getMemberKeys    = "SELECT userKey FROM tCategoryMembers WHERE catID = '__CATNAME__';";
    public static final String getName          = "SELECT username FROM tUser WHERE key = '__KEY__';";
    public static final String getClaims        = "SELECT * FROM tClaim;";
    public static final String getLike          = "SELECT * FROM tLike WHERE parent = '__SIG__';";
    public static final String getComments      = "SELECT * FROM tComment WHERE parent = '__PARENT__';";
    public static final String getComment       = "SELECT * FROM tComment WHERE sig = '__SIG__';";
    
    public static final String addPost           = "INSERT INTO tPost (sig, msgText, time, recieverKey, sendersKey)" +
                                                       "VALUES ('__SIG__', '__msgText__', '__time__', '__recieverKey__', '__sendersKey__');";
    public static final String addPostVisibility = "INSERT INTO tPostVisibleTo (postSig, key)"+
                                                       "VALUES ('__postSig__', '__key__');";
    public static final String addKey            = "INSERT INTO tUser (key) VALUES ('__key__');";
    public static final String newUsername       = "UPDATE tUser SET username = '__name__' WHERE key = '__key__';";
    public static final String removeClaim       = "DELETE FROM tClaim WHERE sig = '__sig__';";
    public static final String addClaim          = "INSERT INTO tClaim (sig, name, claimTime) VALUES ('__sig__', '__name__', '__time__');";
    public static final String addRevocation     = "INSERT INTO tRevocations (sig, timeOfLeak, creationTime) VALUES ('__sig__', '__time__', '__creationTime__');";
    public static final String addPDATA          = "UPDATE tUser SET __field__ = '__value__' WHERE key = '__key__';";
    public static final String addConvo          = "INSERT INTO tConvos (convoID, timeCreated) VALUES ('__sig__', '__time__');";
    public static final String addConvoParticipant = "INSERT INTO tConvoKeys (convoID, key) VALUES ('__sig__', '__key__');";
    public static final String addMessageToConvo = "INSERT INTO tConvoMessages (convoID, sendersKey, msgText, time)"+
                                                       "VALUES ('__convoID__', '__sendersKey__', '__msgText__', '__time__');";
    public static final String addComment        = "INSERT INTO tComment (sig, msgText, parent, creationTime)"+
                                                       "VALUES ('__sig__', '__msgText__', '__parent__', '__creationTime__');";
    public static final String addLike           = "INSERT INTO tLike (likerKey, parent) VALUES ('__likerKey__', '__parent__');";
    public static final String removeLike        = "DELETE FROM tLike WHERE likerKey = '__likerKey__' AND parent = '__parent__';";
    public static final String addEvent          = "INSERT INTO tEvent (sig, startTime, endTime, creatorKey, accepted, name, creationTime)"+
                                                       "VALUES ('__sig__', '__startTime__', '__endTime__', '__creatorKey__', '__accepted__', '__name__', '__creationTime__');";
    public static final String acceptEvent       = "UPDATE tEvent SET accepted = 1 WHERE sig = '__sig__';";
    public static final String declineEvent      = "UPDATE tEvent SET accepted = -1 WHERE sig = '__sig__';";
    public static final String updatePDATApermission = "UPDATE tCategory SET canSeePDATA = __bool__ WHERE catID = '__catID__'";
    public static final String addCategory       = "INSERT INTO tCategory (catID, canSeePDATA) VALUES ('__catID__', __canSeePDATA__);";
    public static final String addToCategory     = "INSERT INTO tCategoryMembers (catID, userKey) VALUES ('__catID__', '__key__');";
}
