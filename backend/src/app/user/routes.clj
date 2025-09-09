(ns app.user.routes
  (:require [app.user.service :as user.service]))

(def RegisterSchema
  [:map
   [:username [:string {:min 3 :max 64}]]
   [:password [:string {:min 8 :max 200}]]])

(def LoginSchema RegisterSchema)

(defn form-auth-routes [env]
  [["/api/auth" {:swagger {:tags ["auth"]}}
    ["/register"
     {:post {:summary "Create user & return JWT"
             :parameters {:body RegisterSchema}
             :handler (fn [{:keys [parameters]}]
                        (user.service/register-user! env (:body parameters)))}}]
    ["/login"
     {:post {:summary    "Verify credentials & return JWT"
             :parameters {:body LoginSchema}
             :handler    (fn [{:keys [parameters]}]
                           (user.service/login-user! env (:body parameters)))}}]]])

(defn form-user-routes [env]
  [["/users" {:swagger {:tags ["users"]}}
    ["" {:name ::users
         :get {:parameters {:query [:map [:q {:optional true} string?]]}
               :handler    (fn [{:keys [identity parameters]}]
                             (let [q (some-> (get-in parameters [:query :q]) clojure.string/trim)]
                               (user.service/search-users! env {:q q :limit 20 :user-id (:user-id identity)})))}}]]])