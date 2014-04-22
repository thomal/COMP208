-- how to add auto-increment column in sqlite: 
-- http://alvinalexander.com/android/sqlite-autoincrement-serial-identity-primary-key


CREATE TABLE tbl_user
(
user_id INTEGER PRIMARY KEY,
username VARCHAR(25),
name VARCHAR(30),
birthday DATE,
sex VARCHAR(1),
email VARCHAR(30),
public_key VARCHAR(8)
);

CREATE TABLE tbl_category(
category_id INTEGER PRIMARY KEY,
name VARCHAR(30)
);

CREATE TABLE tbl_is_in_category
(
is_in_id INTEGER PRIMARY KEY,
category_id INTEGER,
user_id INTEGER,
FOREIGN KEY (category_id) REFERENCES tbl_category(category_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id)
);

CREATE TABLE tbl_private_message
(
message_id INTEGER PRIMARY KEY,
user_from INTEGER,
content VARCHAR(50),
time DATETIME DEFAULT current_timestamp, -- get default!!!!!!
FOREIGN KEY (user_from) REFERENCES tbl_user(user_id)
);

CREATE TABLE tbl_is_in_message
(
is_in_id INTEGER PRIMARY KEY,
time DATETIME DEFAULT current_timestamp, -- default value!!!!
message_id INTEGER,
user_id INTEGER
FOREIGN KEY (message_id) REFERENCES tbl_private_message(message_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id)
);

CREATE TABLE tbl_events
(
event_id INTEGER PRIMARY KEY,
title VARCHAR(10),
content VARCHAR(10),
time DATETIME DEFAULT current_timestamp,
start_date DATETIME,
end_date DATETIME,
user_from INTEGER,
FOREIGN KEY (user_from) REFERENCES tbl_user(user_id)
);

CREATE TABLE tbl_is_invited
(
is_invited_id INTEGER PRIMARY KEY,
user_id INTEGER,
is_in_category_id INTEGER,
event_id INTEGER,
decision BIT DEFAULT NULL, -- DEFAULT VALUE IS NULL
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (is_in_category_id) REFERENCES tbl_is_in_category(is_in_id),
FOREIGN KEY (event_id) REFERENCES tbl_events(event_id)
);

CREATE TABLE tbl_wall_post
(
wall_id INTEGER PRIMARY KEY,
user_from INTEGER,
user_to INTEGER,
content VARCHAR(50),
signature VARCHAR(256),
time DATETIME DEFAULT current_timestamp,
FOREIGN KEY (user_from) REFERENCES tbl_user(user_id),
FOREIGN KEY (user_to) REFERENCES tbl_user(user_id)
);

CREATE TABLE tbl_allowed_to
(
allowed_to_id INTEGER PRIMARY KEY,
user_id INTEGER,
category_id INTEGER,
post_id INTEGER,
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (category_id) REFERENCES tbl_category(category_id),
FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id)
);

CREATE TABLE tbl_has_comment
(
comment_id INTEGER PRIMARY KEY,
comment_content VARCHAR(50),
post_id INTEGER,
user_id INTEGER, -- the one who posts the comment
signature VARCHAR(256),
comment_comment_id INTEGER,
time DATETIME DEFAULT current_timestamp,
FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (comment_comment_id) REFERENCES tbl_has_comment(comment_id)
);

CREATE TABLE tbl_has_like
(
like_id INTEGER PRIMARY KEY,
post_id INTEGER,
user_id INTEGER,
comment_id INTEGER,
time DATETIME DEFAULT current_timestamp,
FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (comment_id) REFERENCES tbl_has_comment(comment_id)
);

CREATE TABLE tbl_message_claim
(
message_claim_id INTEGER PRIMARY KEY,
username VARCHAR(25),
signature VARCHAR(45)
);

CREATE TABLE tbl_login_logout_log
(
log_id INTEGER PRIMARY KEY,
login_time DATETIME DEFAULT current_timestamp,
logout_time DATETIME -- UPDATE THIS COLUMN WHEN USER LOGS OUT
);
