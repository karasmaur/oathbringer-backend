(ns oathbringer.service.auth
  (:require [buddy.sign.jwt :as jwt]
            [buddy.auth.accessrules :refer [success error]]
            [clj-time.core :as time]))

(def secret "b44c0a5b253a2ec35eb9a2c860cd2cd4db09c7e1e799084a")
(def expiration-time 6000)

(defn generate-signature [user]
  (jwt/sign user secret))

(defn unsign-token [token]
  (jwt/unsign token secret))

(defn get-jwt [req]
  "Returns the JWT from a ring request"
  (some->> (get-in req [:headers "authorization"])
           (re-seq #"^Bearer\s+(.*)$")
           first
           second))

(defn generate-expiration-date []
  (time/plus (time/now) (time/seconds expiration-time)))

(defn authenticated-access [request]
  (let [token (get-jwt request)]
    (if (some? token)
      (let [unsigned-token (unsign-token token)]
        (if (= "general" (:permission unsigned-token))
          (success)
          (error "Only authenticated users allowed")))
      (error "Token not informed!"))))

(def rules [{:pattern #"^/api/user/.*"
             :handler (fn [request] (success))
             :request-method :post}
            {:pattern #"^/api/request$"
             :handler authenticated-access}])

(defn on-error
  [request value]
  {:status 403
   :headers {}
   :body (str "Not authorized: " value)})

;; TODO: Check if there's a way to pass the external-id directly to the handler
(defn get-user-external-id [req]
  (:external-id (unsign-token (get-jwt req))))
