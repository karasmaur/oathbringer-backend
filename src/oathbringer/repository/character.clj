(ns oathbringer.repository.character
  (:require [oathbringer.db.core.db-util :refer :all]
            [oathbringer.repository.user :refer [add-character-to-user
                                                 remove-character-from-user
                                                 find-characters-ids-from-user]]
            [oathbringer.repository.campaign :refer [get-campaign-internal-id
                                                     get-campaign-external-id-by-oid]]
            [nano-id.core :refer [nano-id]]
            [clojure.pprint :as pp]))

(def character-collection "characters")

(defn get-char-data [campaign-id char]
  {:external-id (nano-id 10)
   :name (str (char :name))
   :containers []
   :campaign (get-campaign-internal-id campaign-id)})

(defn get-char-dto [char] {:external-id (:external-id char)
                           :name (:name char)
                           :campaign (get-campaign-external-id-by-oid (:campaign char))})

(defn create-character [user-external-id campaign-external-id char]
  (add-character-to-user user-external-id
                         (:_id (save-to-db character-collection
                                           (get-char-data campaign-external-id char)))))

(defn find-character-data [character-id]
  (find-in-db character-collection {:_id (:character_id character-id)}))

(defn get-all-chars-by-user [user-external-id]
  (map get-char-dto (map find-character-data (find-characters-ids-from-user user-external-id))))

(defn update-character [char-external-id char]
  (update-to-db character-collection
                (find-oid-in-db character-collection {:external-id char-external-id})
                char))

(defn delete-character [user-external-id char-external-id]
  (do
    (remove-character-from-user user-external-id
                                (find-oid-in-db character-collection {:external-id char-external-id}))
    (delete-from-db character-collection
                  (find-oid-in-db character-collection {:external-id char-external-id}))))

(defn add-container-to-character [char-external-id container-id]
  (update-embedded-array-to-db character-collection
                               {:external-id char-external-id}
                               {:containers {:container_id container-id}}))

(defn remove-container-from-character [char-external-id container-id]
  (remove-one-from-embedded-array-to-db character-collection
                                        {:external-id char-external-id}
                                        {:containers {:container_id container-id}}))

(defn find-containers-ids-from-char [char-external-id]
  (:containers (find-in-db character-collection {:external-id char-external-id})))