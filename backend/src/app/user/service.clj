(ns app.user.service
  (:require [buddy.hashers :as hashers]
            [app.router.responses :as responses]
            [app.user.db :as user.db]
            [app.auth :as auth])
  (:import (org.postgresql.util PSQLException)))

(defn register-user! [{:keys [ds jwt-secret]} {:keys [username password]}]
  (try
    (let [password-hash (hashers/derive password)
          {id :app_user/id username :app_user/username}
          (user.db/create-user! ds {:username username :password-hash password-hash})
          token         (auth/issue-token {:user-id    id
                                           :jwt-secret jwt-secret
                                           :ttl-seconds 3600})]
      (responses/created {:token token
                          :user {:id id :username username}}))
    (catch PSQLException e
      (if (= "23505" (.getSQLState e))          ;; unique_violation
        (responses/conflict {:error "username-taken"})
        (responses/server-error {:error "db-error"})))
    (catch Exception _
      (responses/server-error {:error "unexpected"}))))

(defn login-user! [{:keys [ds jwt-secret]} {:keys [username password]}]
  (let [{:app_user/keys [id username password_hash] :as row} (user.db/find-user-by-username ds username)]
    (if (and row (hashers/check password password_hash))
      (let [token (auth/issue-token {:user-id     id
                                     :jwt-secret  jwt-secret
                                     :ttl-seconds 3600})]
        (responses/ok {:token token :user {:id id :username username}}))
      (responses/unauthorized {:error "invalid-credentials"}))))
