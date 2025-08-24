(ns app.chats.db
  (:require [app.db :as db]
            [app.util :as util]))

(defn- participant-exists-query [sender-id chat-id]
  {:select [[[:exists
              {:select [1]
               :from   :chat-participant
               :where  [:and
                        [:= :chat-id chat-id]
                        [:= :user-id sender-id]]}]
             :exists]]})

(defn- insert-chat-query [{:keys [kind title]}]
  {:insert-into :chat
   :columns [:kind :title]
   :values  [[kind title]]
   :returning [:id :title]})

(defn- insert-chat-participants-query [chat-id user-ids]
  (let [rows (map (fn [uid] [chat-id uid]) user-ids)]
    {:insert-into :chat-participant
     :values      rows
     :returning   [:chat-id :user-id]}))

(defn- insert-message-query [{:keys [chat-id sender-id body]}]
  {:insert-into :message
   :columns     [:chat-id :sender-id :body]
   :values      [[chat-id sender-id body]]
   :returning   [:id :chat-id :sender-id :body :created-at :edited-at]})

(defn add-chat-participants!
  "Bulk insert participants for a chat. `user-ids` is a seq of longs.
   Returns a vector of inserted rows (empty if all already present)."
  [ds {:keys [chat-id user-ids] :as params}]
  (if (seq user-ids)
    (db/execute! ds (db/format (insert-chat-participants-query chat-id user-ids)))
    []))

(defn create-chat!
  "Insert a chat row. `kind` is `direct` or `group`. `title` optional.
   Returns the inserted row {:id uuid, :kind ..., :title ..., :created_at ...}."
  [ds chat]
  (db/execute-one! ds (db/format (insert-chat-query chat))))

(defn add-new-message!
  "Insert a message into `message`. Returns the inserted row map.
   Expects BIGINT ids (or numeric strings) and non-blank body.
   TODO: prevent empty messages"
  [ds {:keys [chat-id sender-id message-body]}]
  (let [chat-id   (util/->long-safe chat-id)
        sender-id (util/->long-safe sender-id)]
    (db/execute-one! ds
                     (db/format (insert-message-query {:chat-id chat-id
                                                       :sender-id sender-id
                                                       :body message-body})))))

(defn person-in-group-chat? [ds {:keys [sender-id chat-id]}]
  (-> (db/execute-one! ds (db/format (participant-exists-query sender-id chat-id)))
      :exists
      boolean))
