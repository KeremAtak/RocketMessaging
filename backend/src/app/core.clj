(ns app.core
  (:require [org.httpkit.server :as http]
            [app.router.core :as router]))

(defonce server (atom nil))

(defn stop! []
  (when-let [stop @server]
    (stop)
    (reset! server nil)
    :stopped))

(defn start!
  "Start http-kit with the Reitit app."
  [& {:keys [port] :or {port 3000}}]
  (when @server
   (stop!))
  (let [handler (router/app-handler)
        srv (http/run-server handler {:port port})]
    (reset! server srv)
    (println (str "http-kit running on http://localhost:" port))
    :started))

(defn -main [& args]
  (start! args))
