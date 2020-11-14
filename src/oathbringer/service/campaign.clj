(ns oathbringer.service.campaign
  (:require
    [oathbringer.util.service-util :refer :all]
    [oathbringer.repository.campaign :refer :all]
    [oathbringer.service.auth :refer [get-user-external-id]]
    [clojure.pprint :as pp]))

(defn get-campaign-external-id [req]
  (:campaign-id (:params req)))

(defn add-campaign-handler [req]
  (let [campaign (partial get-parameter req)
        user-external-id (get-user-external-id req)]
    (validate-db-return (create-campaign user-external-id campaign)
                        200
                        "Campaign created!")))

(defn update-campaign-handler [req]
  (let [char (get-body-parameters req)
        char-external-id (get-campaign-external-id req)]
    (validate-db-return (update-campaign char-external-id char)
                        200
                        "Campaign updated!")))

(defn delete-campaign-handler [req]
  (validate-db-return (delete-campaign (get-user-external-id req) (get-campaign-external-id req))
                      200
                      "Campaign deleted!"))

(defn get-campaign [req]
  (find-campaign (get-campaign-external-id req)))

(defn get-all-campaigns [req]
  (get-all-campaigns-by-user (get-user-external-id req)))