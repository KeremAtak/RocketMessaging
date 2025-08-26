(ns app.chats.service
  (:require [next.jdbc :as jdbc]
            [app.chats.db :as chats.db]
            [app.router.responses :as responses]))

(defn get-chats!
  "Gets chat messages for a user"
  [ds params]
  (try
    (let [chats (chats.db/list-chats-by-user ds params)]
      (responses/ok chats))
    (catch Exception e
      (responses/server-error (str "Failed to get chats: " (.getMessage e))))))

(defn get-chat-by-id!
  "Gets chat messages for a user by user id"
  [ds params]
  (try
    (let [chat (chats.db/list-by-chat-id ds params)]
      (responses/ok chat))
    (catch Exception e
      (responses/server-error (str "Failed to get chat " (:chat-id params) " :" (.getMessage e))))))

(defn insert-chat-message!
  "Inserts a chat message to a group chat. Assumes that chat exists, and person is part of the group chat."
  [ds {:keys [sender-id chat-id message-body]}]
  (jdbc/with-transaction [tx ds]
    (try
      (if (chats.db/person-in-group-chat? tx {:sender-id sender-id :chat-id chat-id})
        (let [message (chats.db/add-new-message! tx {:sender-id sender-id :chat-id chat-id :message-body message-body})]
          (responses/created message))
        (responses/unauthorized
         {:error (str "Person not part of group chat")}))
      (catch Exception e
        (responses/server-error
         {:error (str "Failed to create message: " (.getMessage e))})))))

(defn create-chat-with-participants!
  "Create a group chat with title and add participants (UUIDs or strings).
   Returns {:chat row, :participants rows}
   TODO: only does group chats at this point.
   TODO: validate that user-ids do exist"
  [ds {:keys [title user-ids]}]
  (jdbc/with-transaction [tx ds]
    (try
      (let [chat (chats.db/create-chat! tx {:kind "group" :title title})
            parts (chats.db/add-chat-participants! tx {:chat-id (:chat/id chat) :user-ids user-ids})]
        (responses/created
         {:chat         chat
          :participants parts}))
      (catch Exception e
        (responses/server-error
         {:error (str "Failed to create chat: " (.getMessage e))})))))
