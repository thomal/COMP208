-- THESE STATEMENTS ARE NOT TESTED. If there are any problems, contact me.
-- aishahsenin@gmail.com, 07984656781

-- addClaim()
--Ported

-- getClaims()
--Ported

-- getUsername()
-- not suppose to SQL form, mike to add

-- addRevocation()
--Ported

-- getRevocations()
--Ported

-- get only selected through specific time [I don't know if this is going to be used, but what the hell]
-- getTimedRevocations
--Ported

-- isRevoked()
-- not suppose to SQL form, mike to add

-- addPData()
--Ported

-- createChat()
--Ported

-- getChat()

-- addToChat()
--Ported

-- addPost()
--Ported

-- getPosts()
--Ported

-- addComment() (Independent)
--Ported

--No Dependent Comment SQL atm

-- getComments() [check further]
-- this SQL only gets comment, does not get post.
--Ported

-- addLike()
--Ported

-- getLikes() / countLikes() any difference?
-- get likes from post
--Ported to getPostLikes

-- get likes from comments
--Ported to getCommentLikes

-- addEvent()
--Ported

-- getEvent()
-- without showing who is invited
--Ported

-- showing who is invited
-- TODO
-- tables involved events, is_invited, user, category
--Ported to getEventWithInvites

-- acceptEvent()
--Ported

-- declineEvent()
--Ported

-- addKey()


-- getKey() [not sure if this is right]
--Ported

-- getName()
-- getting name through public key
--Ported

-- addCategory()
--Ported

-- addToCategory()
--Ported

---------------
-- things to do
---------------
-- auto-increment the primary key columns [DONE]
-- default values for time with current time [DONE]
-- default values for is_invited, decision should be NULL when newly inserted [DONE]
-- default value public key? 
-- note: adding key does not let you see anything but username 
