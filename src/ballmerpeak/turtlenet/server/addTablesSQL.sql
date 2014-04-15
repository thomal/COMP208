-- 1
CREATE TABLE user
(
user_id INT AUTOINCREMENT,
username VARCHAR(25),
name VARCHAR(30),
birthday DATE,
sex VARCHAR(1),
email VARCHAR(30),
public_key VARCHAR(8),
PRIMARY KEY (user_id)
);

-- 2
CREATE TABLE category
(
category_id INT AUTOINCREMENT,
name VARCHAR(30),
PRIMARY KEY (category_id)
);

-- 3
CREATE TABLE is_in_category
(
is_in_id INT AUTOINCREMENT,
category_id INT,
user_id INT,
PRIMARY KEY (is_in_id),
FOREIGN KEY (category_id) REFERENCES category(category_id),
FOREIGN KEY (user_id) REFERENCES user(user_id)
)

-- 4
CREATE TABLE private_message
(
message_id INT AUTOINCREMENT,
from INT,
content VARCHAR(50),
time DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (message_id),
FOREIGN KEY (from) REFERENCES user(user_id)
);

-- 5
CREATE TABLE is_in_message
(
is_in_id INT AUTOINCREMENT,
time DATETIME DEFAULT CURRENT_TIMESTAMP,
message_id INT,
user_id INT,
PRIMARY KEY (is_in_id),
FOREIGN KEY (message_id) REFERENCES private_message(message_id),
FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- 6
CREATE TABLE events
(
event_id INT AUTOINCREMENT,
title VARCHAR(10),
content VARCHAR(10),
time DATETIME DEFAULT CURRENT_TIMESTAMP,
start_date DATETIME,
end_date DATETIME,
from INT,
PRIMARY KEY (event_id),
FOREIGN KEY (from) REFERENCES user(user_id)
);

-- 7
CREATE TABLE is_invited
(
is_invited_id INT AUTOINCREMENT,
user_id INT,
is_in_category_id INT,
event_id INT,
decision BIT,
PRIMARY KEY (is_invited_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (is_in_category_id) REFERENCES is_in_category(is_in_id),
FOREIGN KEY (event_id) REFERENCES events(event_id)
);

-- 8
CREATE TABLE wall_post
(
wall_id INT AUTOINCREMENT,
from INT,
to INT,
content VARCHAR(50),
time DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (wall_id),
FOREIGN KEY (from) REFERENCES user(user_id),
FOREIGN KEY (to) REFERENCES user(user_id)
);

-- 9 
CREATE TABLE allowed_to
(
allowed_to_id INT AUTOINCREMENT,
user_id INT,
category_id INT,
post_id INT,
PRIMARY KEY (allowed_to_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (category_id) REFERENCES category(category_id),
FOREIGN KEY (post_id) REFERENCES wall_post(wall_id)
);

-- 10
CREATE TABLE has_comment
(
comment_id INT AUTOINCREMENT,
comment_content VARCHAR(50),
post_id INT,
user_id INT,
comment_comment_id INT,
time DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (comment_id),
FOREIGN KEY (post_id) REFERENCES wall_post(wall_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (comment_comment_id) REFERENCES has_comment(comment_id)
);

-- 11
CREATE TABLE has_like
(
like_id INT AUTOINCREMENT,
post_id INT,
user_id INT,
comment_id INT,
time DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (like_id),
FOREIGN KEY (post_id) REFERENCES wall_post(wall_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (comment_id) REFERENCES has_comment(comment_id)
);

-- 12
CREATE TABLE message_claim
(
username VARCHAR(25) AUTOINCREMENT,
signature VARCHAR(45),
PRIMARY KEY (username)
);

-- 13
CREATE TABLE key_revoke
(
revoke_id INT AUTOINCREMENT,
signature VARCHAR(45),
time DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (revoke_id),
);

-- 14
CREATE TABLE login_logout_log
(
log_id INT AUTOINCREMENT,
login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
logout_time DATETIME DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY (log_id)
);
