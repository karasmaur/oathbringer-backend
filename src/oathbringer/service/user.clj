(ns oathbringer.service.user
  (:require [oathbringer.repository.user :refer [create find-all]]))

; Helper to get the parameter specified by pname from :params object in req
(defn getparameter [req pname] (get (:params req) pname))

(defn get-all-users-handler [req]
  (find-all))

(defn add-user-handler [req]
  (-> (let [p (partial getparameter req)]
        (create p))))