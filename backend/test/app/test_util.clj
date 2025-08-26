(ns app.test-util
  (:require [clojure.walk :as walk]
            [ring.mock.request :as mock]
            [reitit.ring :as rring]
            [next.jdbc :as jdbc]
            [jsonista.core :as j]
            [migratus.core :refer [migrate]]
            [app.router.core :as app.router]
            [app.auth :as auth]
            [app.config :as app-config]
            [app.user.db :as user.db])

  (:import (java.io InputStream)))

(defonce system* (atom nil))
(defonce test-user*  (atom nil))  ;; holds last created user row
(defonce test-token* (atom nil))  ;; holds its JWT string

(defn start-system!
  "Create the handler and datasourcesrequired with it"
  []
  (let [{:keys [db jwt-secret]} (app-config/read-config)
        ds (app.db/get-datasource db)
        handler (rring/ring-handler (app.router/app-router {:ds ds :jwt-secret jwt-secret}))]
    {:handler handler
     :ds ds
     :jwt-secret jwt-secret}))

(defn system-fixture
  "Wraps the entire test run in a single DB tx that rolls back at the end."
  [f]
  (let [{:keys [db jwt-secret migratus]} (app-config/read-config)
        ds (app.db/get-datasource db)]
    (migrate migratus)
    ;; TODO: rollbacks do not actual work. Probably because it's not enough if you tried transaction as datasource.
    ;; Consider creating own db instance that is wiped after each test run.
    (jdbc/with-transaction [tx ds {:rollback-only true}]
                           ;; Build the app using the transactional *connection*:
      (let [handler (rring/ring-handler (app.router/app-router {:ds tx :jwt-secret jwt-secret}))
            username (str "user-" (System/currentTimeMillis))
            user     (user.db/create-user! tx {:username username
                                               :password-hash "test"})
            token    (auth/issue-token {:user-id (:app_user/id user)
                                        :jwt-secret  jwt-secret
                                        :ttl-seconds 3600})]
        (reset! system* {:handler handler :ds tx :jwt-secret jwt-secret})
        (reset! test-user* user)
        (reset! test-token* token)
        (try
          (f)                                    ; run tests inside the tx
          (finally
            (reset! test-user* nil)
            (reset! test-token* nil)
            (reset! system* nil)))))))

(defn request!
  "Mocks a request to the backend"
  ([method path]
   (request! method path nil))
  ([method path params]
   ((:handler @system*) (mock/request method path params))))

(defn request-authenticated!
  "Mocks an authenticated request. If `body` is a map, it is JSON-encoded."
  ([method path]
   (request-authenticated! method path nil))
  ([method path body]
   (let [req (if (map? body)
               (-> (mock/request method path (j/write-value-as-bytes body))
                   (mock/header "content-type" "application/json")
                   (mock/header "accept" "application/json"))
               (mock/request method path body))
         req (mock/header req "authorization" (str "Bearer " @test-token*))
         resp ((:handler @system*) req)]
     ;; Parses the stream for test validation
     (if (instance? InputStream (:body resp))
       (update resp :body #(-> (j/read-value %)           ; default mapper → string keys
                               (walk/keywordize-keys)))
       resp))))

