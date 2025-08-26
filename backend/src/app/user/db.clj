(ns app.user.db
  (:require [clojure.string :as str]
            [app.db :as db]
            [app.util :as util]))

(defn- select-user-query [user-id]
  {:select [:id]
   :from :app-user
   :where [:= :id (util/->long-safe user-id)]})

(defn find-user-by-id
  "Return minimal user row or nil."
  [ds user-id]
  (db/execute-one! ds (db/format (select-user-query user-id))))

(defn- insert-user-query [{:keys [username password-hash]}]
  {:insert-into :app-user
   :columns     [:username :password-hash]
   :values      [[username password-hash]]
   :returning   [:id :username :created-at]})

(defn create-user!
  "Insert a user. Expects {:username \"...\" :password-hash \"...\"}.
   Returns {:id .. :username .. :created-at ..}."
  [ds {:keys [username password-hash]}]
  (let [username (some-> username str/trim)]
    (when (or (str/blank? username)
              (str/blank? (or password-hash "")))
      (throw (ex-info "username and password-hash are required"
                      {:type ::invalid-user})))
    (db/execute-one! ds
                     (db/format (insert-user-query {:username username
                                                    :password-hash password-hash})))))
