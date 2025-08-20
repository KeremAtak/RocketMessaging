(ns user
  (:require [migratus.core :as migratus]
            [app.config :as app-config]
            [app.core :as app]))

(defn go
  "Start the server. Optionally pass {:migratus-clean true} which resets the database"
  ([] (go {}))
  ([opts]
   (when (:migrate-clean opts)
     (migratus/reset (:migratus (app-config/read-config))))
   (app/start! opts)
   :ready))
