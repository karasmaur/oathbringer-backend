(ns oathbringer.service.user
  (:require [oathbringer.repository.user :refer [create-user find-all-users get-user password-match?]]
            [clojure.data.json :as json]
            [oathbringer.service.auth :refer [generate-signature generate-expiration-date]]
            [clojure.pprint :as pp]
            [oathbringer.util.service-util :refer [get-parameter response-payload]]))

(defn get-all-users-handler [req]
  (find-all-users))

(defn add-user-handler [req]
  (-> (let [user (partial get-parameter req)]
        (create-user user))))

(defn user-login-handler [req]
  "Returns true if the user credentials are correct"
  (let [provided-email (get-parameter req :email)
        provided-password (get-parameter req :password)
        user (get-user provided-email)]
    (pp/pprint (str "User logged in: " provided-email))
    (if-not (nil? user)
      (if (password-match? (:password user) provided-password)
        (let [token-info {:email (:email user)
                          :external-id (:external-id user)
                          :permission "general"
                          :exp (generate-expiration-date)}]
          (response-payload 200 {:token (generate-signature token-info) :message "Signed in!"}))
        (response-payload 406 {:message "Wrong password!"}))
      (response-payload 406 {:message "User doesn't exist!"}))))