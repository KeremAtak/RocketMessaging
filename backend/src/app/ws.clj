;; src/app/ws.clj
(ns app.ws
  (:require [org.httpkit.server :as http]
            [ring.util.codec :as codec]
            [cheshire.core :as json]
            [app.auth :as auth]
            [app.user.db :as user.db]))

;; ch -> {:user-id X}
(defonce clients* (atom {}))
;; user-id -> #{ch}
(defonce user->chs* (atom {}))
;; chat-id -> #{ch}
(defonce chat->chs* (atom {}))

(defn- now [] (java.time.Instant/now))

(defn- add-client! [ch user-id]
  (swap! clients* assoc ch {:user-id user-id})
  (swap! user->chs* update user-id (fnil conj #{}) ch))

(defn- remove-client! [ch]
  (when-let [{:keys [user-id]} (@clients* ch)]
    (swap! user->chs* update user-id disj ch)
    (swap! clients* dissoc ch)
    ;; remove from any chat subscriptions
    (swap! chat->chs* (fn [m] (into {} (for [[cid s] m] [cid (disj s ch)]))))))

(defn subscribe! [ch chat-id]
  (swap! chat->chs* update chat-id (fnil conj #{}) ch))

(defn unsubscribe! [ch chat-id]
  (swap! chat->chs* update chat-id disj ch))

(defn- broadcast!
  "Send JSON payload to all subs of chat-id. If `except` is non-nil, skip that ch."
  [chat-id payload & {:keys [except]}]
  (let [msg (json/generate-string payload)
        chs (get @chat->chs* chat-id)]
    (doseq [ch chs :when (and ch (not= ch except))]
      (http/send! ch msg))))

(defn notify-chat-updated!
  "For updating chat previews in the sidebar."
  [{:keys [chat-id last-message-body last-sender-username last-message-at]}]
  (broadcast! chat-id {:type "chat:updated"
                       :chatId chat-id
                       :lastMessageBody last-message-body
                       :lastSenderUsername last-sender-username
                       :lastMessageAt (some-> last-message-at str)}))

(defn notify-message!
  "Called from your message insert path."
  [{:keys [chat-id message sender]}]
  (broadcast! chat-id {:type "message:new"
                       :chatId chat-id
                       :message message
                       :ts (str (now))})
  (notify-chat-updated! {:chat-id chat-id
                         :last-message-body (:message/body message)
                         :last-sender-username (:username sender)
                         :last-message-at (:message/created_at message)}))

(defn- parse-query [^String qs]
  (when (seq qs) (codec/form-decode qs)))

(defn handler
  "Ring handler for GET /ws?token=JWT. Validates JWT -> binds ch to user-id.
   Client must send JSON messages:
   {\"type\":\"subscribe\",\"chatId\":123}
   {\"type\":\"unsubscribe\",\"chatId\":123}
   {\"type\":\"typing\",\"chatId\":123,\"isTyping\":true}"
  [{:keys [ds jwt-secret]}]
  (fn [req]
    (http/as-channel req
      {:on-open
       (fn [ch]
         (try
           (let [token (some-> req :query-string parse-query (get "token"))
                 claims (when token (auth/unsign-token token jwt-secret))
                 user-id (:sub claims)]
             (if (and user-id (user.db/find-user-by-id ds user-id))
               (do (add-client! ch user-id)
                   (http/send! ch (json/generate-string {:type "ws:ready" :ts (str (now))})))
               (http/close ch)))
           (catch Exception _ (http/close ch))))

       :on-receive
       (fn [ch raw]
         (when-let [{:strs [type chatId isTyping]} (try (json/parse-string raw)
                                                        (catch Exception _ nil))]
           (case type
             "subscribe"   (when (int? chatId) (subscribe! ch chatId))
             "unsubscribe" (when (int? chatId) (unsubscribe! ch chatId))
             "typing"
             (when (int? chatId)
               (let [{:keys [user-id]} (@clients* ch)]
                 (broadcast! chatId {:type "typing"
                                     :chatId chatId
                                     :userId user-id
                                     :isTyping (boolean isTyping)
                                     :ts (str (now))} :except ch)))
             ;; default: ignore
             nil)))

       :on-close
       (fn [ch _status]
         (println "on-close")
         (remove-client! ch))})))
