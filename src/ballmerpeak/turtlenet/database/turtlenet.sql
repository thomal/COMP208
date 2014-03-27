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
-- 9 allowed_to
-- 10 has_like
-- 11 has_comment
-- 12 message_claim
-- 13 key_revoke
-- 14 login_logout_log

-- INCLUDE:
-- auto_increments
-- specify NOT NULLS
-- auto insertion DATETIME

-- create tables
CREATE TABLE user 
(
user_id INT NOT NULL, 
username VARCHAR(25), 
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
category_id INT, -- DATATYPE IS WRONG ON DIAGRAM
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
PRIMARY KEY (event_id),
);

-- error detected, 'is_in_category_id' should be 'category_id'
CREATE TABLE is_invited
(
is_invited_id INT,
user_id INT,
is_in_category INT,
event_id INT,
decision BIT,
PRIMARY KEY (is_invited_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (category_id) REFERENCES category(category_id),
FOREIGN KEY (event_id) REFERENCES events(event_id)
);

CREATE TABLE wall_post
(
post_id INT, -- change this from 'wall_id' to 'post_id'
from INT,
to INT,
content VARCHAR(50),
time DATETIME,
PRIMARY KEY (wall_id),
FOREIGN KEY (from) REFERENCES user(user_id),
FOREIGN KEY (to) REFERENCES user(user_id)
);

CREATE TABLE allowed_to
(
allowed_to_id INT,
user_id INT,
category_id INT,
post_id INT,
PRIMARY KEY (allowed_to_id),
FOREIGN KEY (user_id) REFERENCES user(user_id),
FOREIGN KEY (category_id) REFERENCES category(category_id),
FOREGN
);