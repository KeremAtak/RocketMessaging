(ns app.router.core
  (:require [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(defonce !router (atom nil))

(defn app-router []
  (ring/ring-handler
    (ring/router
      [["/" {:get (fn [_]
                    {:status 200
                     :headers {"Content-Type" "text/plain"}
                     :body "Hello, Reitit!"})}]
       ["/ping" {:get (fn [_]
                        {:status 200
                         :headers {"Content-Type" "text/plain"}
                         :body "pong"})}]])))
