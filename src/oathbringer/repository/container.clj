(ns oathbringer.repository.container
  (:require [clojure.set :refer [rename-keys]]
            [oathbringer.db.core.db-util :refer :all]
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

(defn get-container-dto
  ([container] {:externalId (:external-id container)
                :name (:name container)
                :main (:main container)
                :maxCapacity (:max-capacity container)
                :currentTotalWeight (:current-total-weight container)
                :overEncumbered (:over-encumbered container)})
  ([container items] {:externalId (:external-id container)
                      :name (:name container)
                      :main (:main container)
                      :maxCapacity (:max-capacity container)
                      :currentTotalWeight (:current-total-weight container)
                      :overEncumbered (:over-encumbered container)
                      :items items}))

(defn create-container [char-external-id container]
  (add-container-to-character char-external-id (:_id (save-to-db container-collection (get-container-data container)))))

(defn find-container-data [container-id]
  (find-in-db container-collection {:_id (:container_id container-id)}))

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
  (:items (find-in-db container-collection {:external-id container-external-id})))

(defn get-all-items-from-container [container-external-id]
  (map #(update (rename-keys % {:item_id :item}) :item find-item-data) (find-items-ids-from-container container-external-id)))

(defn calculate-current-total-weight [items]
  (reduce + (map #(* (:quantity %) (:weight (:item %))) items)))

(defn get-container [container-external-id]
  (let [items (get-all-items-from-container container-external-id)
        container-data (find-in-db container-collection {:external-id container-external-id})
        new-current-total-weight (calculate-current-total-weight items)
        new-over-encumbered (> new-current-total-weight (:max-capacity container-data))]
    (get-container-dto
      (assoc container-data :current-total-weight new-current-total-weight :over-encumbered new-over-encumbered)
      items)))