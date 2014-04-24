// INCLUDE:
// auto_increments
// specify NOT NULLS

package ballmerpeak.turtlenet.server;

class DBStrings {
    public static final String[] createDB = {
        "CREATE TABLE tbl_user (                  " +
            "user_id    INTEGER      PRIMARY KEY, " + 
            "username   VARCHAR(25),              " +
            "realname   VARCHAR(30),              " +
            "birthday   DATE,                     " + 
            "sex        VARCHAR(30),              " + 
            "email      VARCHAR(30),              " + 
            "public_key VARCHAR(8),               " +
        ");",
        
        "CREATE TABLE tbl_category (             " +
             "category_id INTEGER   PRIMARY KEY, " +
             "name VARCHAR(30),                  " +
             "view BIT,                          " + // boolean column, 0 for users unable to see, 1 users able to see
        ");",
        
        "CREATE TABLE tbl_is_in_category (                                   " +
            "is_in_id INTEGER       PRIMARY KEY,                             " +
            "category_id INTEGER,                                            " +//DATATYPE IS WRONG ON DIAGRAM (Aishah: All ID's have to be in INTEGER)
            "user_id VARCHAR(50),                                            " +
            "FOREIGN KEY (category_id) REFERENCES tbl_category(category_id)  " +
            "FOREIGN KEY (user_id)     REFERENCES tbl_user(user_id)          " +
        ");",

// column 'from' is not included in private_message message table!
        "CREATE TABLE tbl_private_message (           " +
            "message_id INTEGER PRIMARY KEY,          " +
            "user_from INTEGER                        " +
            "content VARCHAR(50),                     " +
            "time DATETIME DEFAULT current_timestamp, " +
        ");",

        "CREATE TABLE tbl_is_in_message (             " +
            "is_in_id INTEGER PRIMARY KEY,            " +
            "time DATETIME DEFAULT current_timestamp, " +
            "message_id INTEGER,                      " +
            "user_id INTEGER,                         " +
            "FOREIGN KEY (message_id) REFERENCES tbl_private_message(message_id)" +
        ");",

// column 'from' is removed
        "CREATE TABLE tbl_events (                                " +
            "event_id INTEGER PRIMARY KEY,                        " +
            "user_from INTEGER                                    " +
            "title VARCHAR(10),                                   " +
            "content VARCHAR(40),                                 " +
            "time DATETIME DEFAULT current_timestamp,             " +
            "start_date DATETIME,                                 " +
            "end_date DATETIME,                                   " +
            "FOREIGN KEY (user_from) REFERENCES tbl_user(user_id) " +
        ");",

// error detected, 'is_in_category_id' should be 'category_id'
        "CREATE TABLE tbl_is_invited (                                        " +
            "is_invited_id INTEGER PRIMARY KEY,                               " +
            "user_id INTEGER,                                                 " +
            "category_id INTEGER,                                             " + //changed is_in_category_id to category_id 
            "event_id INTEGER,                                                " +
            "decision BIT DEFAULT NULL,                                       " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),              " +
            "FOREIGN KEY (category_id) REFERENCES tbl_category(category_id),  " +
            "FOREIGN KEY (event_id) REFERENCES tbl_events(event_id)           " +
        ");",

        "CREATE TABLE tbl_wall_post (                                 " +
            "wall_id INTEGER PRIMARY KEY,                             " + //change this from 'wall_id' to 'post_id'
            "user_from_id INTEGER,                                    " +
            "user_to_id INTEGER,                                      " +
            "content VARCHAR(50),                                     " +
            "signature VARCHAR(256),                                  " +
            "time DATETIME DEFAULT current_timestamp,                 " +
            "FOREIGN KEY (user_from_id) REFERENCES tbl_user(user_id), " +
            "FOREIGN KEY (user_to_id) REFERENCES tbl_user(user_id)    " +
        ");",


        "CREATE TABLE tbl_allowed_to (                                       " +
            "allowed_to_id INTEGER PRIMARY KEY,                              " +
            "user_id INTEGER,                                                " +
            "category_id INTEGER,                                            " +
            "post_id INTEGER,                                                " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),             " +
            "FOREIGN KEY (category_id) REFERENCES tbl_category(category_id), " +
            "FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id)         " +
        ");",

        "CREATE TABLE tbl_has_comment (                               " +
            "comment_id INTEGER PRIMARY KEY,                          " +
            "comment_content VARCHAR(50),                             " +
            "wall_id INTEGER,                                         " +
            "user_id INTEGER,                                         " +
            "signature VARCHAR(256),                                  " +
            "comment_comment_id INTEGER,                              " +
            "time DATETIME DEFAULT current_timestamp,                 " +
            "FOREIGN KEY (wall_id) REFERENCES tbl_wall_post(wall_id), " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),      " +
            "FOREIGN KEY (comment_comment_id) REFERENCES tbl_has_comment(comment_id)" +
        ");",

        "CREATE TABLE tbl_has_like (                                         " +
            "like_id INTEGER PRIMARY KEY,                                    " +
            "post_id INTEGER,                                                " + 
            "user_id INTEGER,                                                " +
            "comment_id INTEGER,                                             " +
            "time DATETIME DEFAULT current_timestamp,                        " +
            "FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id),        " +
            "FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),             " +
            "FOREIGN KEY (comment_id) REFERENCES tbl_has_comment(comment_id) " +
        ");",

        "CREATE TABLE tbl_message_claim (          " +
            "message_claim_id INTEGER PRIMARY KEY, " +
            "username VARCHAR(25),                 " +
            "signature VARCHAR(45)                 " +
        ");",

        "CREATE TABLE tbl_key_revoke (                " +
            "revoke_id INTEGER PRIMARY KEY,           " +
            "signature VARCHAR(45),                   " +
            "time DATETIME DEFAULT current_timestamp  " +
        ");",

        "CREATE TABLE login_logout_log (                     " +
            "log_id INTEGER PRIMARY KEY,                     " +
            "login_time DATETIME DEFAULT current_timestamp,  " +
            "logout_time DATETIME                            " +
        ");"
    };
    
    public static final String addClaim =

        "INSERT INTO tbl_message_claim (message_claim_id, username, signature) "
        + "VALUES (null, 'userVar', 'sigVar'); " ;

    public static final String getClaims =
        "SELECT username, signature "+
        "FROM tbl_message_claim;" ;

    public static final String getUsername() {
        //I have to JAVA here at some point
    }

    public static final String addRevocation =
        "INSERT INTO tbl_key_revoke (revoke_id, signature) " +
        "VALUES (null, 'sigVar'); " ;

    public static final String getRevocations =
        "SELECT signature, time " +
        "FROM tbl_key_revoke;" ;

    public static final String getTimedRevocations =
        "SELECT signature "+
        "FROM tbl_key_revoke " +
        "WHERE time >= DATETIME('1YYY-MM-DD HH:MM:SS') " +
        "AND time <= DATETIME('2YYY-MM-DD HH:MM:SS'); " ;


    public static final String isRevoked() {
        //Need to do more JAVA Magic here
    }

    public static final String addPData =
        "INSERT INTO tbl_user (user_id, username, realname, birthday, sex, " +
        "email, public_key) " +
        "VALUES (null, 'userVar', 'nameVar', 'YYYY-MM-DD', 'genderVar', 'email@email.com', 'keyVar');" ;

    public static final String getPData =
        "SELECT fieldVar " +
        "FROM tbl_user; " +
        "WHERE public_key = 'keyVar';" ;

    public static final String createChat =
        "INSERT INTO tbl_private_message (message_id, user_from, content) " +
        "VALUES (null, 'userVar', 'contentVar');" ;
    
    public static final String getChat() {
        //empty in the SQLstatements.sql file - assume more JAVA goes here
    }

    public static final String addToChat = 
        "INSERT INTO tbl_is_in_message (is_in_id, message_id, user_id) " +
        "VALUES (NULL, 'messageVar', 'userVar');" ;

    public static final String addPost =
        "INSERT INTO tbl_wall_post (wall_id, user_from, user_to, " +
                                   "content, signature) " +
        "VALUES (NULL, 'userVarFrom', 'userVarTo', 'contentVar', 'sigVar');" ;

    public static final String getPosts = 
        "SELECT user_from, user_to, content " +
        "FROM wall_post " +
        "WHERE wall_id = 'wallVar';" ;

    public static final String addIndependentComment = 
        "INSERT INTO tbl_has_comment (comment_id, comment_content, " +
                                     "wall_id, user_id, signature, " +
                                     "comment_comment_id) " +
        "VALUES (NULL, 'commentVar', 'wallVar', 'userVar', 'sigVar', " +
        "'comment2Var');" ;

    public static final String addRelatedComment =
        //There wasn't any SQL for related comments yet
        "";

    public static final String getComments = 
        "SELECT username, realname, comment_content, time " +
        "FROM tbl_user " +
        "INNER JOIN tbl_has_comment " +
        "ON tbl_user.user_id = tbl_has_comment.user_id " +
        "WHERE wall_id = 'wallVar';" ;
    
    public static final String addLike = 
        "INSERT INTO tbl_has_like (like_id, post_id, user_id, comment_id) " +
        "VALUES (null, 'postVar', 'userVar', 'commentVar');" ;

    public static final String getPostLikes = 
        "SELECT COUNT(like_id) " +
        "FROM tbl_has_like " +
        "WHERE wall_id = 'wallVar';" ;

    public static final String getCommentLikes = 
        "SELECT COUNT(like_id) " +
        "FROM tbl_has_like " +
        "WHERE comment_id = 'commentVar';" ;

    public static final String addEvent = 
        "INSERT INTO tbl_events (event_id, user_from, title, " +
                                "content, start_date, end_date) " +
        "VALUES (null, 'userVar', 'titleVar', 'contentVar', " +
        "'1YYY-MM-DD', '2YYY-MM-DD');" ;

    public static final String getEvent = 
        "SELECT tbl_user.username, tbl_user.realname, tbl_events.title, " +
        "tbl_events.content, tbl_events.time, tbl_events.start_date, " +
        "tbl_events.end_date, tbl_events.user_from " +
        "FROM tbl_user " +
        "INNER JOIN tbl_events " +
        "ON tbl_user.user_id = tbl_events.user_from " +
        "WHERE event_id = 'eventVar';" ;

    public static final String getEventWithInvites = 
        "SELECT tbl_events.title, tbl_events.content, tbl_events.time, " +
        "tbl_events.start_date, tbl_events.end_date, tbl_events.from, " +
        "tbl_user.username, tbl_user.realname " +
        "FROM tbl_events " +
        "INNER JOIN tbl_is_invited " +
        "ON tbl_events.event_id = tbl_is_invited.event_id " +
        "INNER JOIN tbl_user " +
        "ON tbl_is_invited.user_id = tbl_user.user_id " +
        "INNER JOIN tbl_is_in_category " +
        "ON tbl_user.user_id = tbl_is_in_category.user_id " +
        "INNER JOIN tbl_category " +
        "ON tbl_is_in_category.category_id = tbl_category.category_id " +
        "WHERE event_id = 'eventVar';" ;

    public static final String acceptEvent = 
        "UPDATE tbl_is_invited " +
        "SET decision = '1' " +
        "WHERE user_id = 'userVar';" ;

    public static final String declineEvent = 
        "UPDATE tbl_is_invited " +
        "SET decision = '0' " +
        "WHERE user_id = 'userVar';" ;

    public static final String addKey = 
        //Empty at SQLstatements.sql - assuming java goes here
        "";

    public static final String getKey = 
        "SELECT public_key " +
        "FROM tbl_user " +
        "WHERE username = 'userVar';" ;

    public static final String getName = 
        "SELECT username, realname " +
        "FROM tbl_user " +
        "WHERE public_key = 'keyVar';" ;

    public static final String addCategory =
        "INSERT INTO tbl_category (category_id, name) " +
        "VALUES (null, 'nameVar');" ;

    public static final String addToCategory =
        "INSERT INTO tbl_is_in_category (is_in_id, category_id, user_id) " +
        "VALUES (null, 'categoryVar', 'userVar');" ;

}
