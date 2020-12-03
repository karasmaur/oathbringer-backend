(ns oathbringer.service.item
  (:require [oathbringer.util.service-util :refer :all]
            [oathbringer.service.campaign :refer [get-campaign-external-id-from-req]]
            [oathbringer.repository.item :refer :all]))

(defn get-item-external-id [req]
  (:item-id (:params req)))

(defn add-item-handler [req]
  (let [item (partial get-parameter req)
        campaign-external-id (get-campaign-external-id-from-req req)]
    (validate-db-return (create-item campaign-external-id item)
                        200
                        "Item created!")))

(defn update-item-handler [req]
  (let [item (partial get-parameter req)
        item-external-id (:external-id item)]
    (update-item item-external-id item)))

(defn delete-item-handler [req])

(defn get-all-campaign-items-handler [req]
  (get-all-items-by-campaign (get-campaign-external-id-from-req req)))