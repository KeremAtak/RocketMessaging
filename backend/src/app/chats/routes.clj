(ns app.chats.routes
  (:require [app.chats.service :as chats.service]))

(defn form-routes [{:keys [ds]}]
  [["/chats" {:swagger {:tags ["chats"]}}
    ["" {:name ::chats
         :get {:summary "Fetches chats for an user"
               :parameters {:query
                            [:map
                             [:limit  {:optional true} pos-int?]
                             [:offset {:optional true} nat-int?]]}
               :handler (fn [{:keys [parameters identity]}]
                          (let [{:keys [limit offset]} (:query parameters)
                                user-id (:user-id identity)]
                            (chats.service/get-chats! ds {:user-id user-id
                                                          :limit   limit
                                                          :offset  offset})))}}]
    ["/new-chat" {:name ::new-chat
                  :post {:summary    "Create new chat with participants"
                         :parameters {:body [:map
                                             [:userIds [:vector int?]]
                                             [:title string?]]}
                         :handler    (fn [{:keys [identity parameters]}]
                                       (let [title (get-in parameters [:body :title])
                                             user-ids (get-in parameters [:body :userIds])
                                             sender-id    (:user-id identity)]
                                         (chats.service/create-chat-with-participants! ds {:user-ids (conj user-ids sender-id)
                                                                                           :title title})))}}]
    ["/:chat-id/messages" {:name ::messages
                           :get {:summary "List messages in a chat (requires membership)"
                                 :parameters {:path  [:map [:chat-id pos-int?]]
                                              :query [:map
                                                      [:limit     {:optional true} pos-int?]
                                                      [:offset    {:optional true} nat-int?]]}
                                 :handler
                                 (fn [{:keys [parameters identity]}]
                                   (let [user-id (:user-id identity)
                                         chat-id (get-in parameters [:path :chat-id])
                                         {:keys [limit offset]} (:query parameters)]
                                     (chats.service/get-chat-by-id! ds
                                                                    {:user-id user-id
                                                                     :chat-id chat-id
                                                                     :limit limit
                                                                     :offset offset})))}
                           :post {:summary    "Adds a message to an existing chat"
                                  :parameters {:path [:map [:chat-id int?]]
                                               :body [:map [:message-body string?]]}
                                  :handler    (fn [{:keys [identity parameters]}]
                                                (let [chat-id (get-in parameters [:path :chat-id])
                                                      message-body (get-in parameters [:body :message-body])
                                                      sender-id (:user-id identity)]
                                                  (chats.service/insert-chat-message! ds {:chat-id      chat-id
                                                                                          :sender-id    sender-id
                                                                                          :message-body message-body})))}}]]])