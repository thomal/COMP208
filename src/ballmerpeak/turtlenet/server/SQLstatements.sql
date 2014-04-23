-- THESE STATEMENTS ARE NOT TESTED. If there are any problems, contact me.
-- aishahsenin@gmail.com, 07984656781

-- addClaim()
INSERT INTO message_claim (message_claim_id, username, signature)
VALUES (null, 'userVar', 'sigVar');

-- getClaims()
SELECT username, signature
FROM tbl_message_claim;

-- getUsername()
-- not suppose to SQL form, mike to add

-- addRevocation()
INSERT INTO tbl_key_revoke (revoke_id, signature)
VALUES (null, 'sigVar');

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
-- not suppose to SQL form, mike to add

-- addPData()
INSERT INTO user (user_id, username, name, birthday, sex, email, public_key)
VALUES (null, 'username', 'name', 'YYYY-MM-DD', 'F', 'email@email.com', 'publickey');

-- createChat()
INSERT INTO tbl_private_message (message_id, user_from, content)
VALUES (null, 'userId', 'content');

-- getChat()

-- addToChat()
INSERT INTO tbl_is_in_message (is_in_id, message_id, user_id)
VALUES (NULL, 'messageID', 'userID');

-- addPost()
INSERT INTO tbl_wall_post (wall_id, user_from, user_to, content, signature)
VALUES (NULL, 'userIdFrom', 'userIdTo', 'content', 'signature');

-- getPosts()
SELECT user_from, user_to, content
FROM wall_post
WHERE wall_id = 'wallId';

-- addComment()
-- adding new independent comment
INSERT INTO has_comment (comment_id, comment_content, wall_id, user_id, signature, comment_comment_id)
VALUES (NULL, 'commentContent', 'wallId', 'userId', 'signature', 'commentCommentId');

-- getComments() [check further]
-- this SQL only gets comment, does not get post.
SELECT username, name, comment_content, time
FROM tbl_user
INNER JOIN tbl_has_comment
ON tbl_user.user_id = tbl_has_comment.user_id
WHERE wall_id = 'wallId';

-- addLike()
INSERT INTO tbl_has_like (like_id, post_id, user_id, comment_id)
VALUES (null, 'post_id', 'user_id', 'comment_id');

-- getLikes() / countLikes() any difference?
-- get likes from post
SELECT COUNT(like_id)
FROM tbl_has_like
WHERE wall_id = 'wallId';

-- get likes from comments
SELECT COUNT(like_id)
FROM has_like
WHERE comment_id = 'commentId';

-- addEvent()
INSERT INTO tbl_events (event_id, user_from, title, content, start_date, end_date)
VALUES (null, 'userId', 'title', 'content', 'YYYY-MM-DD (start)', 'YYYY-MM-DD (end)');

-- getEvent()
-- without showing who is invited
SELECT tbl_user.username, tbl_user.name, tbl_events.title, tbl_events.content, tbl_events.time, tbl_events.start_date, tbl_events.end_date, tbl_events.user_from 
FROM tbl_user
INNER JOIN events
ON tbl_user.user_id = tbl_events.user_from 
WHERE event_id = 'eventId';

-- showing who is invited
-- TODO
-- tables involved events, is_invited, user, category
SELECT tbl_events.title, tbl_events.content, tbl_events.time, tbl_events.start_date, tbl_events.end_date, tbl_events.from, tbl_user.username, tbl_user.name
FROM tbl_events
INNER JOIN tbl_is_invited
ON tbl_events.event_id = tbl_is_invited.event_id
INNER JOIN tbl_user
ON tbl_is_invited.user_id = tbl_user.user_id
INNER JOIN tbl_is_in_category
ON tbl_user.user_id = tbl_is_in_category.user_id
INNER JOIN tbl_category
ON tbl_is_in_category.category_id = tbl_category.category_id
WHERE event_id = 'eventId';

-- acceptEvent()
UPDATE tbl_is_invited
SET decision = '1'
WHERE user_id = 'user_id here';

-- declineEvent()
UPDATE tbl_is_invited
SET decision = '0'
WHERE user_id = 'user_id here';

-- addKey()


-- getKey() [not sure if this is right]
SELECT public_key
FROM user
WHERE username = 'username';

-- getName()
-- getting name through public key
SELECT username, name
FROM user
WHERE public_key = 'publickey';

-- addCategory()
INSERT INTO tbl_category (category_id, name)
VALUES (null, 'name');

-- addToCategory()
INSERT INTO tbl_is_in_category (is_in_id, category_id, user_id)
VALUES
(null, 'category_id', 'user_id');

---------------
-- things to do
---------------
-- auto-increment the primary key columns [DONE]
-- default values for time with current time [DONE]
-- default values for is_invited, decision should be NULL when newly inserted [DONE]
-- default value public key? 
-- note: adding key does not let you see anything but username 