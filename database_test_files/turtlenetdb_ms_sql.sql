CREATE TABLE tbl_user
(
user_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
username VARCHAR(25),
name VARCHAR(30),
birthday DATE,
sex VARCHAR(1),
email VARCHAR(30),
public_key VARCHAR(8)
)

CREATE TABLE tbl_category
(
category_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
name VARCHAR(30)
)

CREATE TABLE tbl_is_in_category
(
is_in_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
category_id INT,
user_id INT,
FOREIGN KEY (category_id) REFERENCES tbl_category(category_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id)
)

CREATE TABLE tbl_private_message
(
message_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
user_from INT,
content VARCHAR(50),
time DATETIME DEFAULT GETDATE(),
FOREIGN KEY (user_from) REFERENCES tbl_user(user_id)
)


CREATE TABLE tbl_is_in_message
(
is_in_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
time DATETIME DEFAULT GETDATE(),
message_id INT,
user_id INT,
FOREIGN KEY (message_id) REFERENCES tbl_private_message(message_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id)
)

CREATE TABLE tbl_events
(
event_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
title VARCHAR(10),
content VARCHAR(10),
time DATETIME DEFAULT GETDATE(),
start_date DATETIME,
end_date DATETIME,
user_from INT,
FOREIGN KEY (user_from) REFERENCES tbl_user(user_id)
)


CREATE TABLE tbl_is_invited
(
is_invited_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
user_id INT,
is_in_category_id INT,
event_id INT,
decision BIT,
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (is_in_category_id) REFERENCES tbl_is_in_category(is_in_id),
FOREIGN KEY (event_id) REFERENCES tbl_events(event_id)
)


CREATE TABLE tbl_wall_post
(
wall_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
user_from INT,
user_to INT,
content VARCHAR(50),
time DATETIME DEFAULT GETDATE(),
FOREIGN KEY (user_from) REFERENCES tbl_user(user_id),
FOREIGN KEY (user_to) REFERENCES tbl_user(user_id)
)


CREATE TABLE tbl_allowed_to
(
allowed_to_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
user_id INT,
category_id INT,
post_id INT,
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (category_id) REFERENCES tbl_category(category_id),
FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id)
)

CREATE TABLE tbl_has_comment
(
comment_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
comment_content VARCHAR(50),
post_id INT,
user_id INT,
comment_comment_id INT,
time DATETIME DEFAULT GETDATE(),
FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (comment_comment_id) REFERENCES tbl_has_comment(comment_id)
)

CREATE TABLE tbl_has_like
(
like_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
post_id INT,
user_id INT,
comment_id INT,
time DATETIME DEFAULT GETDATE(),
FOREIGN KEY (post_id) REFERENCES tbl_wall_post(wall_id),
FOREIGN KEY (user_id) REFERENCES tbl_user(user_id),
FOREIGN KEY (comment_id) REFERENCES tbl_has_comment(comment_id)
)


CREATE TABLE tbl_message_claim
(
username VARCHAR(25) NOT NULL PRIMARY KEY,
signature VARCHAR(45)
)


CREATE TABLE tbl_key_revoke
(
revoke_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
signature VARCHAR(45),
time DATETIME DEFAULT GETDATE()
)


CREATE TABLE tbl_login_logout_log
(
log_id INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
login_time DATETIME DEFAULT GETDATE(),
logout_time DATETIME DEFAULT GETDATE()
)

