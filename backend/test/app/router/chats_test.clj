(ns app.router.chats-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [app.test-util :refer [system-fixture test-user* system* request-authenticated!]]
            [app.user.db :as user.db]))

(use-fixtures :once system-fixture)

(deftest chat-tests
  (testing "insert chat"
    ;; create another user to include as a participant
    (let [{:keys [ds]} @system*
          u2       (user.db/create-user! ds {:username (str "user-" (System/currentTimeMillis) "-b")
                                             :password-hash "test"})
          payload {:title     "Test Chat"
                   :user-ids  [(:app_user/id u2)]}
          {:keys [status body]}
          (request-authenticated! :post "/api/chats/new-chat" payload)]
      (is (= 201 status))
      (is (= "Test Chat" (get-in body [:chat :title])))
      (is (int? (get-in body [:chat :id])))
      (is (= 2 (count (:participants body)))))

    (testing "inserted chat found in db"
      (let [{:keys [status body]} (request-authenticated! :get "/api/chats")]
        (is (= 200 status))
        (is (= 1 (count body)))
        (is (= "Test Chat" (:title (first body))))
        (is (int? (:id (first body))))))))
