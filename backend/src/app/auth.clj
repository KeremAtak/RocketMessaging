(ns app.auth
  (:require [buddy.sign.jwt :as jwt]
            [app.config :as app-config])
  (:import (java.time Instant)))

(defn unsign-token [token jwt-secret]
  (jwt/unsign token jwt-secret {:alg :hs256}))

(defn issue-token-hs256
  "Return a JWT string. `secret` is a byte[] or string. `ttl-seconds` e.g. 3600."
  [{:keys [user-id ttl-seconds iss aud]
    :or {ttl-seconds 3600}}]
  (let [secret (:jwt-secret (app-config/read-config))
        now (Instant/now)
        claims {:sub (str user-id)                 ; subject (user id)
                :iss iss                           ; issuer, optional
                :aud aud                           ; audience, optional
                :iat (.getEpochSecond now)         ; issued-at (seconds)
                :exp (+ (.getEpochSecond now) ttl-seconds)}] ; expiry
    (jwt/sign claims secret {:alg :hs256})))

(comment
  (app.auth/issue-token {:user-id 1}))