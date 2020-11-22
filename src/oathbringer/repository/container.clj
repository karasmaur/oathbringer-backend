(ns oathbringer.repository.container
  (:require [oathbringer.db.core.db-util :refer :all]
            [oathbringer.repository.item :refer :all]
            [oathbringer.repository.character :refer [add-container-to-character
                                                      remove-container-from-character
                                                      find-containers-ids-from-char]]
            [nano-id.core :refer [nano-id]]))

(def container-collection "containers")

(defn get-container-data [container] {:external-id (nano-id 10)
                                      :name (str (container :name ))
                                      :main (container :main)
                                      :max-capacity (container :maxCapacity)
                                      :current-total-weight (container :currentTotalWeight)
                                      :over-encumbered (container :overEncumbered)
                                      :items []})

(defn get-container-dto [container] {:external-id (get container "external-id")
                                     :name (get container "name")
                                     :main (get container "main")
                                     :maxCapacity (get container "max-capacity")
                                     :currentTotalWeight (get container "current-total-weight")
                                     :overEncumbered (get container "over-encumbered")
                                     :items (get container "items")})

(defn create-container [char-external-id container]
  (add-container-to-character char-external-id (:_id (save-to-db container-collection (get-container-data container)))))

(defn find-container-data [container-id]
  (find-in-db container-collection {:_id (get container-id "container_id")}))

(defn get-all-containers-by-char [char-external-id]
  (map get-container-dto (map find-container-data (find-containers-ids-from-char char-external-id))))

(defn update-container [container-external-id container]
  (update-to-db container-collection
                (find-oid-in-db container-collection {:external-id container-external-id})
                container))

(defn delete-container [char-external-id container-external-id]
  (do
    (remove-container-from-character char-external-id
                                     (find-oid-in-db container-collection {:external-id container-external-id}))
    (delete-from-db container-collection
                    (find-oid-in-db container-collection {:external-id container-external-id}))))

(defn remove-item-from-container [container-external-id item-internal-id]
  (remove-one-from-embedded-array-to-db container-collection
                                        {:external-id container-external-id}
                                        {:items {:item_id item-internal-id}}))

(defn add-item-to-container [container-external-id item-internal-id quantity]
  (do
    (remove-item-from-container container-external-id item-internal-id)
    (update-embedded-array-to-db-no-duplicate container-collection
                                              {:external-id container-external-id}
                                              {:items {:item_id item-internal-id :quantity quantity}})))

(defn find-items-ids-from-container [container-external-id]
  (get (find-in-db container-collection {:external-id container-external-id}) "items"))

(defn get-all-items-from-container [container-external-id]
  (map get-item-dto (map find-item-data (:_id (find-items-ids-from-container container-external-id)))))