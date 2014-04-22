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
        "CREATE TABLE private_message ( " +
            "message_id INT NOT NULL,   " +
            "content VARCHAR(50),       " +
            "time DATETIME,             " +
            "PRIMARY KEY (message_id)   " +
        ");",

        "CREATE TABLE is_in_message ( " +
            "is_in_id INT NOT NULL,   " +
            "time DATETIME,           " +
            "message_id INT,          " +
            "user_id INT,             " +
            "PRIMARY KEY (is_in_id),  " +
            "FOREIGN KEY (message_id) REFERENCES private_message(message_id)" +
        ");",

// column 'from' is removed
        "CREATE TABLE events (      " +
            "event_id INT NOT NULL, " +
            "title VARCHAR(10),     " +
            "content VARCHAR(40),   " +
            "time DATETIME,         " +
            "start_date DATETIME,   " +
            "end_date DATETIME,     " +
            "PRIMARY KEY (event_id) " +
        ");",

// error detected, 'is_in_category_id' should be 'category_id'
        "CREATE TABLE is_invited (                                        " +
            "is_invited_id INT NOT NULL,                                  " +
            "user_id INT,                                                 " +
            "category_id INT,                                             " + //changed is_in_category_id to category_id 
            "event_id INT,                                                " +
            "decision BIT,                                                " +
            "PRIMARY KEY (is_invited_id),                                 " +
            "FOREIGN KEY (user_id) REFERENCES user(user_id),              " +
            "FOREIGN KEY (category_id) REFERENCES category(category_id),  " +
            "FOREIGN KEY (event_id) REFERENCES events(event_id)           " +
        ");",

        "CREATE TABLE wall_post (                                 " +
            "post_id INT NOT NULL,                                " + //change this from 'wall_id' to 'post_id'
            "user_from_id INT,                                    " +
            "user_to_id INT,                                      " +
            "content VARCHAR(50),                                 " +
            "time DATETIME,                                       " +
            "PRIMARY KEY (post_id),                               " +
            "FOREIGN KEY (user_from_id) REFERENCES user(user_id), " +
            "FOREIGN KEY (user_to_id) REFERENCES user(user_id)    " +
        ");",


        "CREATE TABLE allowed_to (                                       " +
            "allowed_to_id INT NOT NULL,                                 " +
            "user_id INT,                                                " +
            "category_id INT,                                            " +
            "post_id INT,                                                " +
            "PRIMARY KEY (allowed_to_id),                                " +
            "FOREIGN KEY (user_id) REFERENCES user(user_id),             " +
            "FOREIGN KEY (category_id) REFERENCES category(category_id), " +
            "FOREIGN KEY (post_id) REFERENCES wall_post(post_id)         " +
        ");",

        "CREATE TABLE has_comment (                               " +
            "comment_id INT NOT NULL,                             " +
            "comment_content VARCHAR(50),                         " +
            "post_id INT,                                         " +
            "user_id INT,                                         " +
            "comment_comment_id INT,                              " +
            "time DATETIME,                                       " +
            "PRIMARY KEY (comment_id),                            " +
            "FOREIGN KEY (post_id) REFERENCES wall_post(post_id), " +
            "FOREIGN KEY (user_id) REFERENCES user(user_id),      " +
            "FOREIGN KEY (comment_comment_id) REFERENCES has_comment(comment_id)" +
        ");",

        "CREATE TABLE has_like (                                         " +
            "like_id INT NOT NULL,                                       " +
            "post_id INT,                                                " + 
            "user_id INT,                                                " +
            "comment_id INT,                                             " +
            "time DATETIME,                                              " +
            "PRIMARY KEY (like_id),                                      " +
            "FOREIGN KEY (post_id) REFERENCES wall_post(post_id),        " +
            "FOREIGN KEY (user_id) REFERENCES user(user_id),             " +
            "FOREIGN KEY (comment_id) REFERENCES has_comment(comment_id) " +
        ");",

        "CREATE TABLE message_claim (       " +
            "username VARCHAR(25) NOT NULL, " +
            "signature VARCHAR(45),         " +
            "PRIMARY KEY (username)         " +
        ");",

        "CREATE TABLE key_revoke (   " +
            "revoke_id INT NOT NULL, " +
            "signature VARCHAR(45),  " +
            "time DATETIME,          " +
            "PRIMARY KEY (revoke_id) " +
        ");",

        "CREATE TABLE login_logout_log ( " +
            "log_id INT NOT NULL,        " +
            "login_time DATETIME,        " +
            "logout_time DATETIME,       " +
            "PRIMARY KEY (log_id)        " +
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
