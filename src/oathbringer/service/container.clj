(ns oathbringer.service.container
  (:require
    [oathbringer.util.service-util :refer :all]
    [oathbringer.repository.container :refer :all]
    [oathbringer.service.character :refer [get-char-external-id]]
    [oathbringer.service.auth :refer [get-user-external-id]]
    [clojure.pprint :as pp]))

(defn get-container-external-id [req]
  (:container-id (:params req)))

(defn add-container-handler [req]
  (let [container (partial get-parameter req)
        char-external-id (get-char-external-id req)]
    (validate-db-return (create-container char-external-id container)
                        200
                        "Container created!")))

(defn update-container-handler [req]
  (let [container (get-body-parameters req)
        container-external-id (get-container-external-id req)]
    (validate-db-return (update-container container-external-id container)
                        200
                        "Container updated!")))

(defn delete-container-handler [req]
  (validate-db-return (delete-container (get-char-external-id req) (get-container-external-id req))
                      200
                      "Container deleted!"))

(defn get-all-containers [req]
  (get-all-containers-by-char (get-char-external-id req)))