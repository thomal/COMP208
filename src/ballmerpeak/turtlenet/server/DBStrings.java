package ballmerpeak.turtlenet.server;

class DBStrings {
    public static final String[] createDB = {
        "CREATE TABLE tConvos ("+
        "convoID     INT AUTOINCREMENT,"+
        "sig         TEXT,"+
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
        "text       TEXT,"+
        "time       TEXT,"+
        "PRIMARY KEY (pk));",
        
        "CREATE TABLE tPost ("+
        "postID      INT AUTOINCREMENT,"+
        "text        TEXT,"+
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
        "catID       INT AUTOINCREMENT,"+
        "name        TEXT,"+
        "canSeePDATA INT,"+                  //1 if category can see pdata, 0 otherwise
        "PRIMARY KEY (catID));",
        
        "CREATE TABLE tCategoryMembers ("+
        "pk      INT AUTOINCREMENT,"+
        "catID   INT,"+
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
        "commentID    INT AUTOINCREMENT,"+
        "text         TEXT,"+
        "parent       TEXT,"+                 //sig of thing being commented
        "sig          TEXT,"+
        "commenterKey TEXT,"+
        "creationTime TEXT,"+
        "PRIMARY KEY (commentID));",
    };
}
