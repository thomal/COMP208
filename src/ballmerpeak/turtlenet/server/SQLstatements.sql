-- THESE STATEMENTS ARE NOT TESTED. If there are any problems, contact me.
-- aishahsenin@gmail.com, 07984656781

-- addClaim()
INSERT INTO message_claim (claimID, username, signature)
VALUES
(
NULL, -- claimID
'userVar', -- username
'sigVar' -- signature
);

-- getClaims()
SELECT username, signature
FROM message_claim;

-- getUsername()
SELECT username
FROM message_claim
WHERE signature = 'sigVar';

-- addRevocation()
INSERT INTO key_revoke (revokeID, signature)
VALUES
(
NULL,
'sigVar'
);

-- getRevocations()
-- get the whole revocation list
SELECT signature, time
FROM key_revoke

-- get only selected through specific time [I don't know if this is going to be used, but what the hell]
-- getTimedRevocations
SELECT signature
FROM key_revoke
WHERE time >= DATETIME('2014-01-01 00:00:00')  --MIKE_IS_HERE
AND time <= DATETIME('2014-12-31 00:00:00')
-- NOTE: SQLite do not store datetime as datetime, instead it stores as string. 

-- isRevoked()
INSERT INTO key_revoke (signature)
VALUES
(
'signature'
);

-- addPData()
INSERT INTO user (username, name, birthday, sex, email)
VALUES
(
'username',
'name',
'1990-01-01',
'F' -- drop down option preferable F - female, M - male
'email@email.com'
);

-- createChat()

-- getChat()

-- addToChat()

-- addPost()
INSERT INTO wall_post (from, to, content)
VALUES
(
'user_id',
'user_id',
'something else'
);

-- getPosts()
SELECT from, to, content
FROM wall_post

-- addComment()
-- adding new independent comment
INSERT INTO has_comment (comment_content, post_id, user_id)
VALUES
(
'something',
'post_id',
'user_id'
);

-- adding new comment onto a comment
INSERT INTO has_comment (comment_content, post_id, user_id, comment_comment_id)
(
'something',
'post_id',
'user_id',
'comment_comment_id'
);

-- getComments()
SELECT username, name, comment_content, time
FROM user
INNER JOIN has_comment
ON user.user_id = has_comment.user_id
WHERE post_id = 'variable';

-- addLike()
INSERT INTO has_like (post_id, user_id, comment_id)
VALUES
(
'post_id',
'user_id',
'comment_id'
);

-- getLikes() / countLikes() any difference?
-- get likes from post
SELECT COUNT(like_id)
FROM has_like
WHERE post_id = 'variable';

-- get likes from comments
SELECT COUNT(like_id)
FROM has_like
WHERE comment_id = 'variable';

-- addEvent()
INSERT INTO events (title, content, start_date, end_date, from)
VALUES
(
'title',
'content',
'start_date',
'end_date',
'user_id'
);

-- getEvent()
-- without showing who is invited
SELECT user.username, user.name, events.title, events.content, events.time, events.start_date, events.end_date, events.from 
FROM user
INNER JOIN events
ON user.user_id = events.from 
WHERE event_id = 'variable';

-- showing who is invited
-- TODO
-- tables involved events, is_invited, user, category
SELECT events.title, events.content, events.time, events.start_date, events.end_date, events.from, user.username, user.name
FROM events
INNER JOIN is_invited
ON events.event_id = is_invited.event_id
INNER JOIN user
ON is_invited.user_id = user.user_id
INNER JOIN is_in_category
ON user.user_id = is_in_category.user_id
INNER JOIN category
ON is_in_category.category_id = category.category_id
WHERE event_id = 'state event id here';

-- acceptEvent()
UPDATE is_invited
SET decision = '1'
WHERE user_id = 'user_id here';

-- declineEvent()
UPDATE is_invited
SET decision = '0'
WHERE user_id = 'user_id here';

-- addKey()


-- getKey()
SELECT public_key
FROM user
WHERE username = 'something_something_bla_bla_turtlepoop';

-- getName()
-- getting name through public key
SELECT username, name
FROM user
WHERE public_key = 'publickey';

-- addCategory()
INSERT INTO category (name)
VALUES
(
'name'
);

-- addToCategory()
INSERT INTO is_in_category (category_id, user_id)
VALUES
(
'category_id',
'user_id'
);

---------------
-- things to do
---------------
-- auto-increment the primary key columns 
-- default values for time with current time
-- default values for is_invited, decision should be NULL when newly inserted
-- default value public key? 
-- note: adding key does not let you see anything but username 
