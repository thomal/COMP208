-- using linux terminal, run sqlite with 'sqlite3 turtlenet'

-- list of tables
-- 1 user [done]
-- 2 category [done] 
-- 3 is_in_category [done, changes made]
-- 4 private_message [done, changes made]
-- 5 is_in_message [done]
-- 6 events [done, changes made]
-- 7 is_invited [done, changes made]
-- 8 wall_post [done]
-- 9 allowed_to [done]
-- 10 has_like [done]
-- 11 has_comment [done]
-- 12 message_claim [done]
-- 13 key_revoke [done]
-- 14 login_logout_log [done]

-- INCLUDE:
-- auto_increments
-- specify NOT NULLS

-- create tables
CREATE TABLE user 
(
user_id INT NOT NULL, 
username VARCHAR(25) NOT NULL, 
name VARCHAR(30), 
birthday DATE, 
sex VARCHAR(1), 
email VARCHAR(30), 
public_key VARCHAR(8),
PRIMARY KEY (user_id)
);

CREATE TABLE category
(
category_id INT NOT NULL,
name VARCHAR(30),
PRIMARY KEY (category_id)
);

CREATE TABLE is_in_category
(
is_in_id INT NOT NULL,
category_id INT NOT NULL, -- DATATYPE IS WRONG ON DIAGRAM
user_id VARCHAR(50),
PRIMARY KEY (is_in_id),
FOREIGN KEY (category_id) REFERENCES category(category_id)
);

-- column 'from' is not included in private_message message table!
CREATE TABLE private_message
(
message_id INT NOT NULL,
content VARCHAR(50),
time DATETIME,
PRIMARY KEY (message_id)
);

CREATE TABLE is_in_message
(
is_in_id INT NOT NULL,
time DATETIME,
message_id INT,
user_id INT,
PRIMARY KEY (is_in_id),
FOREIGN KEY (message_id) REFERENCES private_message(message_id)
);

-- column 'from' is removed
CREATE TABLE events
(
event_id INT NOT NULL,
title VARCHAR(10),
content VARCHAR(40),
time DATETIME,
start_date DATETIME,
end_date DATETIME,
PRIMARY KEY (event_id)
);

-- error detected, 'is_in_category_id' should be 'category_id'
CREATE TABLE is_invited
(
is_invited_id INT NOT NULL,
user_id INT,
category_id INT, -- changed is_in_category_id to category_id
event_id INT,
decision BIT,
PRIMARY KEY (is_invited_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (category_id) REFERENCES category(category_id),
FOREIGN KEY (event_id) REFERENCES events(event_id)
);

CREATE TABLE wall_post
(
post_id INT NOT NULL, -- change this from 'wall_id' to 'post_id'
user_from_id INT,
user_to_id INT,
content VARCHAR(50),
time DATETIME,
PRIMARY KEY (post_id),
FOREIGN KEY (user_from_id) REFERENCES user(user_id),
FOREIGN KEY (user_to_id) REFERENCES user(user_id)
);


CREATE TABLE allowed_to
(
allowed_to_id INT NOT NULL,
user_id INT,
category_id INT,
post_id INT,
PRIMARY KEY (allowed_to_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (category_id) REFERENCES category(category_id),
FOREIGN KEY (post_id) REFERENCES wall_post(post_id)
);

CREATE TABLE has_comment
(
comment_id INT NOT NULL,
comment_content VARCHAR(50),
post_id INT,
user_id INT, 
comment_comment_id INT,
time DATETIME,
PRIMARY KEY (comment_id),
FOREIGN KEY (post_id) REFERENCES wall_post(post_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (comment_comment_id) REFERENCES has_comment(comment_id)
);

CREATE TABLE has_like
(
like_id INT NOT NULL,
post_id INT, 
user_id INT,
comment_id INT,
time DATETIME,
PRIMARY KEY (like_id),
FOREIGN KEY (post_id) REFERENCES wall_post(post_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (comment_id) REFERENCES has_comment(comment_id)
);

CREATE TABLE message_claim
(
username VARCHAR(25) NOT NULL,
signature VARCHAR(45),
PRIMARY KEY (username)
);

CREATE TABLE key_revoke
(
revoke_id INT NOT NULL,
signature VARCHAR(45),
time DATETIME,
PRIMARY KEY (revoke_id)
);

CREATE TABLE login_logout_log
(
log_id INT NOT NULL,
login_time DATETIME,
logout_time DATETIME,
PRIMARY KEY (log_id)
);

