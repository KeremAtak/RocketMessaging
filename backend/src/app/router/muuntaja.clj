(ns app.router.muuntaja
  (:require [muuntaja.core :as m]
            [clojure.string :as str]))

(defn- camelize [s]                       ; "created_at" -> "createdAt"
  (let [[h & t] (str/split s #"_")]
    (apply str h (map str/capitalize t))))

(def json-encoder-opts
  {:encode-key-fn
   (fn [k]
     (let [s (name k)                     ; :chat/created_at -> "chat/created_at"
           s (if-let [i (str/index-of s "/")] (subs s (inc i)) s)]
       (camelize s)))})

;; The purpose here is to convert data to more acceptable http response format
(def muuntaja
  (m/create
   (-> m/default-options
       (assoc-in [:formats "application/json" :encoder-opts] json-encoder-opts))))
