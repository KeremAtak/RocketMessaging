(ns app.router.core
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.malli :as coercion.malli]
            [reitit.ring.middleware.muuntaja :as rrmm]
            [reitit.ring.middleware.parameters :as rrmp]
            [malli.transform :as mt]
            [app.router.muuntaja :as app.muuntaja]
            [app.middleware.auth :as middleware.auth]
            [app.chats.routes :as chats.routes]
            [app.user.routes :as user.routes]))

(def router-config
  {:data {:coercion (coercion.malli/create
                     {:transformers {:body   {:default mt/json-transformer}    ; for request bodies
                                     :string {:default mt/string-transformer}  ; for path/query/headers
                                     :response {:default mt/json-transformer} ; optional, for responses
                                     }})
          :muuntaja app.muuntaja/muuntaja
          :middleware [rrmp/parameters-middleware
                       rrmm/format-negotiate-middleware
                       rrmm/format-response-middleware   ;; <-- encodes response bodies
                       rrmm/format-request-middleware
                       rrc/coerce-exceptions-middleware
                       rrc/coerce-request-middleware
                       rrc/coerce-response-middleware]
          :securityDefinitions {:BearerAuth
                                {:type "apiKey"
                                 :name "Authorization"
                                 :in   "header"}}}})

(defn app-router
  "Creates the router, consuming the configuration"
  [env]
  (ring/router
   [["" {:no-doc  true
         :swagger {:securityDefinitions
                   {:BearerAuth
                    {:type "apiKey"
                     :name "Authorization"
                     :in   "header"}}}}
     ["/ping" {:name ::ping
               :get  {:summary "Used for health checks"
                      :handler (fn [_]
                                 {:status  200
                                  :headers {"Content-Type" "text/plain"}
                                  :body    "pong"})}}]
     ["/swagger.json" {:get {:swagger  {:info {:title    "RocketMessaging backend Api"
                                               :basePath "/"}}
                             :basePath "/"
                             :handler  (swagger/create-swagger-handler)}}]]
    (user.routes/form-routes env)
    ["/api"
     {:middleware [[middleware.auth/wrap-jwt-auth env]]
      :swagger    {:security [{:BearerAuth []}]}}
     (chats.routes/form-routes env)]]
   (merge {:data {:ds (:ds env)}} router-config)))

(defn app-handler
  "Creates the application handler, consuming the router and helpers such as swagger.json."
  [env]
  (ring/ring-handler
   (app-router env)
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"})
    (ring/create-default-handler))))
