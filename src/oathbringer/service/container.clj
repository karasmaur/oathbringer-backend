(ns oathbringer.service.container
  (:require
    [oathbringer.util.service-util :refer :all]
    [oathbringer.repository.container :refer :all]
    [oathbringer.repository.item :refer :all]
    [oathbringer.service.campaign :refer [get-campaign-external-id-from-req]]
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

(defn add-item-to-container-handler [req]
  (let [container-external-id (get-container-external-id req)
        campaign-external-id (get-campaign-external-id-from-req req)
        item (:params req)]
    (if (contains? item :external-id)
      (validate-db-return (add-item-to-container container-external-id
                                                 (do
                                                   (update-item (:external-id item) item)
                                                   (get-item-internal-id (:external-id item)))
                                                 (:quantity item))
                          200
                          "Item added")
      (validate-db-return (add-item-to-container container-external-id
                                                 (:_id (create-item campaign-external-id item))
                                                 (:quantity item))
                          200
                          "Item added"))))

(defn get-all-container-items-handler [req]
  (get-all-items-from-container (get-container-external-id req)))