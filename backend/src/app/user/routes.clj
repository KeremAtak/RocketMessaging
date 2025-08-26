(ns app.user.routes
  (:require [app.user.service :as user.service]))

(def RegisterSchema
  [:map
   [:username [:string {:min 3 :max 64}]]
   [:password [:string {:min 8 :max 200}]]])

(def LoginSchema RegisterSchema)

(defn form-routes [env]
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
