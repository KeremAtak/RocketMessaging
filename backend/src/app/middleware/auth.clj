(ns app.middleware.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as str]
            [app.router.responses :as responses]
            [app.auth :as auth]
            [app.user.db :as user.db]
            [app.util :as util]))

;; TODO: this is not fully secure. Should doublecheck validations to ensure issuer, expiration and whatnot works as intended.
(defn wrap-jwt-auth
  "Middleware that validates JWT tokens in the Authorization header.
   On success, it adds `:identity` with the token claims to the request."
  [handler {:keys [ds jwt-secret]}]
  (fn [request]
    (let [auth-header (get-in request [:headers "authorization"])
          token       (when (and auth-header
                                 (str/starts-with? auth-header "Bearer "))
                        (subs auth-header 7))] ;; strips "Bearer "
      (if token
        (try
          (let [claims (auth/unsign-token token jwt-secret)
                sub (:sub claims)]
            (if (user.db/find-user-by-id ds sub)
              (handler (assoc request :identity {:user-id (util/->long-safe sub)}))
              (throw Exception)))
          (catch Exception e
            (responses/unauthorized "Invalid or expired token")))
        ;; If no token, continue without identity or reject
        (responses/unauthorized "Missing Authorization headern")))))
