(ns app.router.core
  (:require [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.middleware.muuntaja :as ring.muuntaja]
            [muuntaja.core :as m]))

(def router-config
  (let [muuntaja (m/create m/default-options)]
    {:data {:muuntaja m/instance
            :middleware [ring.muuntaja/format-middleware]}}))

(defn app-router
  "Creates the router, consuming the configuration"
  []
  (ring/router
   [["" {:no-doc true}
     ["/ping" {:name ::ping
               :get  {:summary "Used for health checks"
                      :handler (fn [_]
                                 {:status  200
                                  :headers {"Content-Type" "text/plain"}
                                  :body    "pong"})}}]
     ["/swagger.json" {:get {:swagger {:info {:title    "RocketMessaging backend Api"
                                              :basePath "/"}}
                             :handler (swagger/create-swagger-handler)}}]]
    ["/api"
     ["/foo" {:swagger {:tags ["Example"]}}
      ["" {:name ::foo
           :get {:summary "TODO: Remove once actual endpoints added"
                 :responses {:status 200 :body string?}
                 :handler (fn [_]
                            {:status  200
                             :headers {"Content-Type" "text/plain"}
                             :body    "bar"})}}]]]]
   router-config))

(defn app-handler
  "Creates the application handler, consuming the router and helpers such as swagger.json."
  []
  (ring/ring-handler
   (app-router)
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/swagger-ui"})
    (ring/create-default-handler))))
