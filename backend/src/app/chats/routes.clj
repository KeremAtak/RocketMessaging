(ns app.chats.routes
  (:require [app.chats.service :as chats.service]))

(defn form-routes [{:keys [ds]}]
  [["/chats" {:swagger {:tags ["chats"]}}
    ["/new-chat" {:name ::new-chat
                  :post {:summary    "Create new chat with participants"
                         :parameters  {:body [:map
                                              [:user-ids [:vector int?]]
                                              [:title string?]]}
                         :handler    (fn [{:keys [identity parameters] :as request}]
                                       (let [title (get-in parameters [:body :title])
                                             user-ids (get-in parameters [:body :user-ids])
                                             sender-id    (:user-id identity)]
                                         (chats.service/create-chat-with-participants! ds {:user-ids (conj user-ids sender-id)
                                                                                           :title title})))}}]
    ["/:chat-id/messages" {:name ::messages
                           :post {:summary    "Adds a message to an existing chat"
                                  :parameters  {:path [:map [:chat-id int?]]
                                                :body [:map [:message-body string?]]}
                                  :handler    (fn [{:keys [identity parameters]}]
                                                (let [chat-id      (get-in parameters [:path :chat-id])
                                                      message-body (get-in parameters [:body :message-body])
                                                      sender-id    (:user-id identity)]
                                                  (chats.service/insert-chat-message! ds {:chat-id   chat-id
                                                                                          :sender-id sender-id
                                                                                          :message-body message-body})))}}]]])