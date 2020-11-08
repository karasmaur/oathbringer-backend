(ns oathbringer.service.user
  (:require [oathbringer.repository.user :refer :all]
            [oathbringer.service.auth :refer :all]
            [clojure.pprint :as pp]
            [oathbringer.util.service-util :refer :all]))

(defn add-user-handler [req]
  (response-payload 200 (-> (let [user (partial get-parameter req)]
                              (create-user user)))))

(defn update-user-handler [req]
  (let [user (get-body-parameters req)
        user-external-id (get-user-external-id req)]
    (validate-db-return (update-character user-external-id user)
                        200
                        "User updated!")))

(defn delete-user-handler [req]
  (validate-db-return (delete-user (get-user-external-id req))
                      200
                      "User deleted!"))

(defn user-login-handler [req]
  "Returns true if the user credentials are correct"
  (let [provided-email (get-parameter req :email)
        provided-password (get-parameter req :password)
        user (find-user provided-email)]
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