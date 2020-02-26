(ns oathbringer.service.user
  (:require [oathbringer.repository.user :refer [add-user get-all-users]]))

; Helper to get the parameter specified by pname from :params object in req
(defn getparameter [req pname] (get (:params req) pname))

(defn get-all-users-handler [req]
  (get-all-users))

(defn add-user-handler [req]
  (-> (let [p (partial getparameter req)]
        (add-user {:user/firstname    (str (p :firstname))
                        :user/surname (str (p :surname))}))))