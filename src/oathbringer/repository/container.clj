(ns oathbringer.repository.container
  (:require [oathbringer.util.db-util :refer :all]
            [oathbringer.repository.character :refer [add-container-to-character
                                                      remove-container-from-character
                                                      find-containers-ids-from-char]]
            [nano-id.core :refer [nano-id]]))

(def container-collection "containers")

(defn get-container-data [container] {:external-id (nano-id 10)
                                      :name (str (container :name ))
                                      :main (container :main)
                                      :max-capacity (container :max-capacity)
                                      :current-total-weight (container :current-total-weight)
                                      :over-encumbered (container :over-encumbered)
                                      :items []})

(defn get-container-dto [container] {:external-id (get container "external-id")
                                     :name (get container "name")
                                     :main (get container "main")
                                     :max-capacity (get container "max-capacity")
                                     :current-total-weight (get container "current-total-weight")
                                     :over-encumbered (get container "over-encumbered")
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