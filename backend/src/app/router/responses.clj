(ns app.router.responses)

(defn response [status-code body]
  {:status status-code
   :body body})

(defn created
  ([]
   (created ""))
  ([body]
   (response 201 body)
   {:status  201
    :body    body}))

(defn unauthorized
  ([]
   (unauthorized ""))
  ([body]
   (response 401 body)
   {:status  401
    :body    body}))

(defn server-error
  ([]
   (server-error ""))
  ([body]
   (response 500 body)))
