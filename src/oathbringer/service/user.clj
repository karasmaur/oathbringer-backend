(ns oathbringer.service.user
  (:require [oathbringer.repository.user :refer [create-user find-all-users email-exists? password-match?]]
            [clojure.data.json :as json]
            [oathbringer.service.auth :refer [generate-signature generate-expiration-date]]
            [clojure.pprint :as pp]
            [oathbringer.util.service-util :refer [get-parameter response-payload]]))

(defn get-all-users-handler [req]
  (find-all-users))

(defn add-user-handler [req]
  (-> (let [user (partial get-parameter req)]
        (create-user user))))

;; TODO: Add the user-external-id to the token.
(defn user-login-handler [req]
  "Returns true if the user credentials are correct"
  (let [email ((partial get-parameter req) :email)
        password ((partial get-parameter req) :password)
        token-info {:email email :permission "general" :exp (generate-expiration-date)}]
    (pp/pprint (str "User logged in: " email))
    (if (email-exists? email)
      (if (password-match? email password)
        (response-payload 200 {:token (generate-signature token-info) :message "Signed in!"})
        (response-payload 406 {:message "Wrong password!"}))
      (response-payload 406 {:message "User doesn't exist!"}))))