(ns oathbringer.repository.character
  (:require [oathbringer.util.db-util :refer :all]
            [oathbringer.util.service-util :refer [response-payload]]
            [oathbringer.repository.user :refer [add-character-to-user
                                                 remove-character-from-user
                                                 find-characters-ids-from-user]]
            [nano-id.core :refer [nano-id]]
            [clojure.pprint :as pp]))

(def character-collection "characters")

(defn get-char-data [char] {:external-id (nano-id 10)
                            :name (str (char :name))
                            :holders []})

(defn get-char-dto [char] {:external-id (get char "external-id")
                           :name (get char "name")
                           :holders (get char "holders")})

(defn create-character [user-external-id char]
  (add-character-to-user user-external-id (:_id (save-to-db character-collection (get-char-data char)))))

(defn find-character-data [character-id]
  (find-in-db character-collection {:_id (get character-id "character_id")}))

(defn get-all-chars-by-user [user-external-id]
  (map get-char-dto (map find-character-data (find-characters-ids-from-user user-external-id))))

(defn update-character [char-external-id char]
  (update-to-db character-collection
                (find-oid-in-db character-collection {:external-id char-external-id})
                char))

(defn delete-character [char-external-id]
  (delete-from-db character-collection
                  (find-oid-in-db character-collection {:external-id char-external-id})))