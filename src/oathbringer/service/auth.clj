(ns oathbringer.service.auth
  (:require [buddy.sign.jwt :as jwt]
            [buddy.auth.backends :as backends]
            [buddy.auth.accessrules :refer [success error]]))

(def secret "b44c0a5b253a2ec35eb9a2c860cd2cd4db09c7e1e799084a")
(def backend (backends/jws {:secret secret}))

(defn generate-signature [user]
  (jwt/sign user secret))

(defn unsign-token [token]
  (jwt/unsign token secret))

(defn get-jwt
  "get the JWT from a ring request"
  [req]
  (some->> (get-in req [:headers "authorization"])
           (re-seq #"^Bearer\s+(.*)$")
           first
           second))

(defn get-permission [token] (:permission (unsign-token token)))

(defn authenticated-access-2 [request]
  (let [token (get-jwt request)]
    (print token)))

(defn authenticated-access [request]
  (if (get-permission (get-jwt request))
    (success)
    (error "Only authenticated users allowed")))

(defn admin-access [request]
  (if (= "admin" (get-permission (:token request)))
    true
    (error "Only admins users allowed")))

(def rules [{:pattern #"^/api/user/.*"
             :handler (fn [request] (success))
             :request-method :post}
            {:pattern #"^/api/request$"
             :handler authenticated-access}])

(defn on-error
  [request value]
  {:status 403
   :headers {}
   :body "Not authorized"})