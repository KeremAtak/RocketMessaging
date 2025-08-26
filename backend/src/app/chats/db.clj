(ns app.chats.db
  (:require [app.db :as db]
            [app.util :as util]))

(defn- list-by-chat-query [{:keys [user-id chat-id limit offset]}]
  {:select   [:m.id :m.chat_id :m.sender_id :m.body :m.created_at :m.edited_at]
   :from     [[:message :m]]
   :join     [[:chat_participant :p] [:= :p.chat_id :m.chat_id]]
   :where    [:and
              [:= :p.user_id user-id]
              [:= :m.chat_id chat-id]]
   :order-by [[:m.created_at :desc] [:m.id :desc]]
   :limit    (or limit 50)
   :offset   (or offset 0)})

(defn- list-chats-query [user-id {:keys [limit offset]}]
  {:select [:c.id :c.kind :c.title :c.created-at]
   :from   [[:chat :c]]
   :join   [[:chat-participant :p] [:= :p.chat-id :c.id]]
   :where  [:= :p.user-id user-id]
   :order-by [[:c.created-at :desc]]
   :limit  (or limit 50)
   :offset (or offset 0)})

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

(defn list-chats-by-user
  "Returns a vector of {:id :kind :title :created-at} for the user."
  [ds {:keys [user-id limit offset]}]
  ;; Unqualified, lower-case keys are friendlier for the frontend:
  (db/execute! ds (db/format (list-chats-query user-id {:limit limit :offset offset}))))

(defn list-by-chat-id
  "Returns messages visible to `user-id` in `chat-id`, newest first.
   Supports optional {:limit, :offset,."
  [ds {:keys [user-id chat-id limit offset] :as opts}]
  (db/execute! ds
               (db/format (list-by-chat-query opts))))

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
