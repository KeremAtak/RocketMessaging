(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [app.core :as app]))

(defn go
  "Start the server. Optionally pass {:port N}."
  ([] (go {:port 3000}))
  ([opts]
   (app/start! opts)
   :ready))

(defn reset!
  "Stop server, reload changed namespaces, then (user/go)."
  []
  (app/stop!)
  (refresh :after 'user/go))
