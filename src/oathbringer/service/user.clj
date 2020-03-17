(ns oathbringer.service.user
  (:require [oathbringer.repository.user :refer [create-user find-all-users email-exists? password-match?]]
            [clojure.data.json :as json]
            [oathbringer.service.auth :refer [generate-signature generate-expiration-date]]))

(defn response-payload [status body]
  "Returns a map accepted by the http server. To be used on response with json objects as body."
  {:status  status
   :headers {"Content-Type" "text/json"}
   :body    (json/write-str body)})

; Helper to get the parameter specified by pname from :params object in req
(defn getparameter [req pname] (get (:params req) pname))

(defn get-all-users-handler [req]
  (find-all-users))

(defn add-user-handler [req]
  (-> (let [user (partial getparameter req)]
        (create-user user))))

(defn user-login-handler [req]
  "Returns true if the user credentials are correct"
  (let [email ((partial getparameter req) :email)
        password ((partial getparameter req) :password)
        token-info {:email email :permission "general" :exp (generate-expiration-date)}]
    (if (email-exists? email)
      (if (password-match? email password)
        (response-payload 200 {:token (generate-signature token-info) :message "Signed in!"})
        (response-payload 406 {:message "Wrong password!"}))
      (response-payload 406 {:message "User doesn't exist!"}))))