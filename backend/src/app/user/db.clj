(ns app.user.db
  (:require [clojure.string :as str]
            [app.db :as db]
            [app.util :as util]))

(defn- select-user-query [query-by-key param]
  {:select [:id :username :password-hash]
   :from :app-user
   :where [:= query-by-key param]})

(defn- search-users-query
  [q limit user-id]
  (let [q (or q "")
        lim (-> (or limit 20) (max 1) (min 100))
        like [:ilike :username (str "%" q "%")]
        where (if user-id
                [:and like [:<> :id (util/->long-safe user-id)]]
                like)]
    {:select   [:id :username]
     :from     :app-user
     :where    where
     :order-by [[:username :asc]]
     :limit    lim}))

(defn find-user-by-id [ds user-id]
  (db/execute-one! ds (db/format (select-user-query :id (util/->long-safe user-id)))))

(defn find-user-by-username [ds username]
  (db/execute-one! ds (db/format (select-user-query :username username))))

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

(defn search-users!
  "Return users matching `q` (case-insensitive) as [{:id :username} ...].
   When `q` is blank/nil, returns []. Optional keys: :limit (default 20), :exclude-user-id."
  [ds {:keys [q limit user-id]}]
  (let [q* (some-> q str/trim)]
    (if (str/blank? q*)
      []
      (db/execute! ds (db/format (search-users-query q* limit user-id))))))
