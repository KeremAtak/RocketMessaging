(ns app.core
  (:require [org.httpkit.server :as http]
            [migratus.core :as migratus]
            [app.config :as app-config]
            [app.router.core :as router]))

(defonce server (atom nil))

(defn stop! []
  (when-let [srv @server]
    (println "Stopping http-kit")
    (srv) ;; http-kit servers are stopped by calling them with no args
    (reset! server nil)
    :stopped))

(defn start!
  "Start http-kit with the Reitit app."
  [& [opts]]
  (stop!)
  (let [{:keys [app] :as config} (app-config/read-config)
        handler (router/app-handler)
        port (:port app)
        srv (http/run-server handler {:port port})]
    (reset! server srv)
    (migratus/migrate (:migratus config))
    (println (str "http-kit running on http://localhost:" port))
    :started))

(defn -main [& args]
  (start! args))
