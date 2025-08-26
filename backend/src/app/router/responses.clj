(ns app.router.responses)

(defn response [status-code body]
  {:status status-code
   :body body})

(defn ok
  ([]
   (ok ""))
  ([body]
   (response 200 body)))

(defn created
  ([]
   (created ""))
  ([body]
   (response 201 body)))

(defn unauthorized
  ([]
   (unauthorized ""))
  ([body]
   (response 401 body)))

(defn server-error
  ([]
   (server-error ""))
  ([body]
   (response 500 body)))
