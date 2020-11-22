(ns oathbringer.repository.item
  (:require [oathbringer.db.core.db-util :refer :all]
            [nano-id.core :refer [nano-id]]
            [oathbringer.repository.campaign :refer [get-campaign-internal-id
                                                     get-campaign-external-id-by-oid]]))

(def item-collection "items")

(defn get-item-data [campaign-id item]
  {:external-id (nano-id 10)
   :name (str (item :name))
   :weight (item :weight)
   :campaign (get-campaign-internal-id campaign-id)})

(defn get-item-data-for-update [item]
  {:name (str (item :name))
   :weight (item :weight)})

(defn get-item-dto [item] {:external-id (get item :external-id)
                           :name (get item :name)
                           :weight (get item :weight)})

(defn create-item [campaign-external-id item]
  (save-to-db item-collection
              (get-item-data campaign-external-id item)))

(defn update-item [item-external-id item]
  (update-to-db item-collection
                (find-oid-in-db item-collection {:external-id item-external-id})
                (get-item-data-for-update item)))

(defn get-item-internal-id [item-external-id]
  (find-oid-in-db item-collection {:external-id item-external-id}))

(defn get-all-items-by-campaign [campaign-external-id]
  (map get-item-dto (find-all-in-db item-collection {:campaign (get-campaign-internal-id campaign-external-id)})))

(defn find-item-data [container-id]
  (find-in-db item-collection {:_id container-id}))