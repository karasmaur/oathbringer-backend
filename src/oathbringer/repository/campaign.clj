(ns oathbringer.repository.campaign
  (:require [oathbringer.db.core.db-util :refer :all]
            [oathbringer.repository.user :refer [add-campaign-to-user
                                                 remove-campaign-from-user
                                                 find-campaigns-ids-from-user]]
            [nano-id.core :refer [nano-id]]))

(def campaign-collection "campaigns")

(defn get-campaign-data [campaign] {:external-id (nano-id 10)
                                    :name (str (campaign :name))
                                    :game-master (str (campaign :gameMaster))
                                    :game-type (str (campaign :gameType))})

(defn get-campaign-dto [campaign] {:external-id (:external-id campaign)
                                   :name (:name campaign)
                                   :gameMaster (:game-master campaign)
                                   :gameType (:game-type campaign)})

(defn create-campaign [user-external-id campaign]
  (add-campaign-to-user user-external-id (:_id (save-to-db campaign-collection (get-campaign-data campaign)))))

(defn find-campaign [campaign-external-id]
  (get-campaign-dto (find-in-db campaign-collection {:external-id campaign-external-id})))

(defn find-campaign-data [campaign-id]
  (find-in-db campaign-collection {:_id (:campaign_id campaign-id)}))

(defn get-campaign-internal-id [campaign-external-id]
  (find-oid-in-db campaign-collection {:external-id campaign-external-id}))

(defn get-campaign-external-id-by-oid [campaign-internal-id]
  (find-oid-in-db campaign-collection {:id campaign-internal-id}))

(defn get-all-campaigns-by-user [user-external-id]
  (map get-campaign-dto (map find-campaign-data (find-campaigns-ids-from-user user-external-id))))

(defn update-campaign [campaign-external-id campaign]
  (update-to-db campaign-collection
                (get-campaign-internal-id campaign-external-id)
                campaign))

(defn delete-campaign [user-external-id campaign-external-id]
  (do
    (remove-campaign-from-user user-external-id
                               (get-campaign-internal-id campaign-external-id))
    (delete-from-db campaign-collection
                    (get-campaign-internal-id campaign-external-id))))