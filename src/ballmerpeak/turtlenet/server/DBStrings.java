// INCLUDE:
// auto_increments
// specify NOT NULLS

package ballmerpeak.turtlenet.server;

class DBStrings {
    public static final String[] createDB = {
        "CREATE TABLE tbl_user (                  " +
            "user_id    INTEGER      PRIMARY KEY, " + 
            "username   VARCHAR(25),              " +
            "name       VARCHAR(30),              " +
            "birthday   DATE,                     " + 
            "sex        VARCHAR(1),               " + 
            "email      VARCHAR(30),             " + 
            "public_key VARCHAR(8),              " +
        ");",
        
        "CREATE TABLE tbl_category (        " +
             "category_id INTEGER   PRIMARY KEY, " +
             "name VARCHAR(30),                  " +
        ");",
        
        "CREATE TABLE tbl_is_in_category ( " +
            "is_in_id INTEGER       PRIMARY KEY, " +
            "category_id INTEGER,                " +//DATATYPE IS WRONG ON DIAGRAM (Aishah: All ID's have to be in INTEGER)
            "user_id VARCHAR(50),                " +
            "FOREIGN KEY (category_id) REFERENCES tbl_category(category_id)  " +
            "FOREIGN KEY (user_id)     REFERENCES tbl_user(user_id)          " +
        ");",

// column 'from' is not included in private_message message table!
        "CREATE TABLE tbl_private_message ( " +
            "message_id INTEGER PRIMARY KEY,   " +
            "user_from INTEGER" +
            "content VARCHAR(50),       " +
            "time DATETIME DEFAULT current_timestamp,             " +
        ");",

        "CREATE TABLE tbl_is_in_message ( " +
            "is_in_id INTEGER PRIMARY KEY,   " +
            "time DATETIME DEFAULT current_timestamp,           " +
            "message_id INTEGER,          " +
            "user_id INTEGER,             " +
            "FOREIGN KEY (message_id) REFERENCES tbl_private_message(message_id)" +
        ");",

// column 'from' is removed
        "CREATE TABLE tbl_events (      " +
            "event_id INTEGER PRIMARY KEY, " +
            "user_from INTEGER" +
            "title VARCHAR(10),     " +
            "content VARCHAR(40),   " +
            "time DATETIME DEFAULT current_timestamp,         " +
            "start_date DATETIME,   " +
            "end_date DATETIME,     " +
            "FOREIGN KEY (user_from) REFERENCES tbl_user(user_id)" +
        ");",

// error detected, 'is_in_category_id' should be 'category_id'
        "CREATE TABLE tbl_is_invited (                                        " +
            "is_invited_id INTEGER PRIMARY KEY,                                  " +
            "user_id INTEGER,                                                 " +
            "category_id INTEGER,                                             " + //changed is_in_category_id to category_id 
            "event_id INTEGER,                                                " +
            "decision BIT DEFAULT NULL,                                                " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),              " +
            "FOREIGN KEY (category_id) REFERENCES tbl_category(category_id),  " +
            "FOREIGN KEY (event_id) REFERENCES tbl_events(event_id)           " +
        ");",

        "CREATE TABLE tbl_wall_post (                                 " +
            "wall_id INTEGER PRIMARY KEY,                                " + //change this from 'wall_id' to 'post_id'
            "user_from_id INTEGER,                                    " +
            "user_to_id INTEGER,                                      " +
            "content VARCHAR(50),                                 " +
            "signature VARCHAR(256),                                 " +
            "time DATETIME DEFAULT current_timestamp,                                       " +
            "FOREIGN KEY (user_from_id) REFERENCES tbl_user(user_id), " +
            "FOREIGN KEY (user_to_id) REFERENCES tbl_user(user_id)    " +
        ");",


        "CREATE TABLE tbl_allowed_to (                                       " +
            "allowed_to_id INTEGER PRIMARY KEY,                                 " +
            "user_id INTEGER,                                                " +
            "category_id INTEGER,                                            " +
            "post_id INTEGER,                                                " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),             " +
            "FOREIGN KEY (category_id) REFERENCES tbl_category(category_id), " +
            "FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id)         " +
        ");",

        "CREATE TABLE tbl_has_comment (                               " +
            "comment_id INTEGER PRIMARY KEY,                             " +
            "comment_content VARCHAR(50),                         " +
            "wall_id INTEGER,                                         " +
            "user_id INTEGER,                                         " +
            "signature VARCHAR(256)," +
            "comment_comment_id INTEGER,                              " +
            "time DATETIME DEFAULT current_timestamp,                                       " +
            "FOREIGN KEY (wall_id) REFERENCES tbl_wall_post(wall_id), " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),      " +
            "FOREIGN KEY (comment_comment_id) REFERENCES tbl_has_comment(comment_id)" +
        ");",

        "CREATE TABLE tbl_has_like (                                         " +
            "like_id INTEGER PRIMARY KEY,                                       " +
            "post_id INTEGER,                                                " + 
            "user_id INTEGER,                                                " +
            "comment_id INTEGER,                                             " +
            "time DATETIME DEFAULT current_timestamp,                                              " +
            "FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id),        " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),             " +
            "FOREIGN KEY (comment_id) REFERENCES tbl_has_comment(comment_id) " +
        ");",

        "CREATE TABLE tbl_message_claim (       " +
            "message_claim_id INTEGER PRIMARY KEY, " +
            "username VARCHAR(25), " +
            "signature VARCHAR(45)        " +
        ");",

        "CREATE TABLE tbl_key_revoke (   " +
            "revoke_id INTEGER PRIMARY KEY, " +
            "signature VARCHAR(45),  " +
            "time DATETIME DEFAULT current_timestamp          " +
        ");",

        "CREATE TABLE login_logout_log ( " +
            "log_id INTEGER PRIMARY KEY,        " +
            "login_time DATETIME DEFAULT current_timestamp,        " +
            "logout_time DATETIME       " +
        ");"
    };
    
    public static final String addClaim =
        "INSERT INTO message_claim (claimID, username, signature)"+
        "VALUES ("+
        "        NULL,"+
        "        'userVar',"+
        "        'sigVar'"+
        "        );";

    public static final String getClaims =
        "SELECT username, signature"+
        "FROM message_claim;";

    public static final String getUsername =
        "SELECT username"+
        "FROM message_claim"+
        "WHERE signature = 'sigVar'";

    public static final String addRevocation =
        "INSERT INTO key_revoke (revokeID, signature)"+
        "VALUES ("+
                "NULL,"+
                "'sigVar'"+
                ");";

    public static final String getRevocations =
        "SELECT signature, time"+
        "FROM key_revoke";

    public static final String getTimedRevocations =
        "SELECT signature"+
        "FROM key_revoke"+
        "WHERE time >= DATETIME('timeBeginVar')"+ //MIKE_IS_HERE
        "AND time <= DATETIME('2014-12-31 00:00:00')";


    public static final String isRevoked =
        "INSERT INTO key_revoke (signature)"+
        "VALUES ("+
        "        'signature'"+
        "        );";

    public static final String addPData =
        "INSERT INTO user (username, name, birthday, sex, email)"+
        "VALUES ("+
        "        'username',"+
        "        'name',"+
        "        'YYYY-MM-DD',"+
        "        'genderChar'"+ //-- drop down option preferable F - female, M - male
        "        'email@email.com'"+
        "        );";

    public static final String addChat = "";

    public static final String addToChat = "";

    public static final String addPost = "";

    public static final String getPosts = "";

    public static final String addIndependentComment = "";

    public static final String addRelatedComment = "";

    public static final String getComments = "";
    
    public static final String addLike = "";

    public static final String getPostLikes = "";

    public static final String getCommentLikes = "";

    public static final String addEvent = "";

    public static final String getEvent = "";

    public static final String getEventWithInvites = "";

    public static final String acceptEvent = "";

    public static final String declineEvent = "";

    public static final String addKey = "";

    public static final String getKey = "";

    public static final String getName = "";

}
