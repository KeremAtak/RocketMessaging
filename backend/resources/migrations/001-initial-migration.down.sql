-- Drop indexes on message
DROP INDEX IF EXISTS idx_msg_chat_iddesc;
--;;

-- Drop message table
DROP TABLE IF EXISTS message;
--;;

-- Drop participant index
DROP INDEX IF EXISTS idx_participant_user;
--;;

-- Drop chat_participant
DROP TABLE IF EXISTS chat_participant;
--;;

-- Drop direct chat indexes
DROP INDEX IF EXISTS idx_direct_by_user_a;
--;;

DROP INDEX IF EXISTS idx_direct_by_user_b;
--;;

-- Drop direct_chat
DROP TABLE IF EXISTS direct_chat;
--;;

-- Drop chat
DROP TABLE IF EXISTS chat;
--;;

-- Drop app_user
DROP TABLE IF EXISTS app_user;
--;;
