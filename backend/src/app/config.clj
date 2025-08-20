(ns app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]))

(defn read-config
  "Reads resources/system.edn with optional {:profile :dev|:prod ...}."
  ([] (read-config {:profile :dev}))
  ([opts] (aero/read-config (io/resource "system.edn") opts)))