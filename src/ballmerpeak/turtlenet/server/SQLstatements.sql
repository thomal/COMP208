-- THESE STATEMENTS ARE NOT TESTED. If there are any problems, contact me.
-- aishahsenin@gmail.com, 07984656781

-- addClaim()
INSERT INTO message_claim (username, signature)
VALUES
(
variable1, -- username
variable2 -- signature
);

-- getClaims()
SELECT username, signature
FROM message_claim;

-- getUsername()
SELECT username
FROM message_claim
WHERE signature = 'variable1';

-- addRevocation()
-- do not have to add revoke_id, it is auto-incremented
INSERT INTO key_revoke (signature, time)
VALUES
(
'variable1',
'variable2',
'variable3'
);

getRevocations()
-- get the whole revocation list
SELECT signature, time
FROM key_revoke

-- get only selected through specific time
SELECT signature
FROM key_revoke
WHERE time >= DATETIME('2014-01-01 00:00:00') 
AND time <= DATETIME('2014-12-31 00:00:00')
-- NOTE: SQLite do not store datetime as datetime, instead it stores as string. 

-- isRevoked()
-- not sure what is the difference between this and previous function

-- addPData()
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

-- acceptEvent()

declineEvent()
addKey()
getKey()
getName()
addCategory()
addToCategory()

---------------
-- things to do
---------------
-- auto-increment the primary key columns 
-- default values for time with current time