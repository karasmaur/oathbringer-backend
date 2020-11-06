(ns oathbringer.repository.character
  (:require [oathbringer.util.db-util :refer [save-to-db find-in-db find-oid update-embedded-array-to-db]]
            [oathbringer.util.service-util :refer [response-payload]]
            [oathbringer.repository.user :refer [add-character]]
            [nano-id.core :refer [nano-id]]
            [clojure.pprint :as pp]))

(def character-collection "characters")

(defn get-char-data [char] {:external-id (nano-id 10)
                            :name (str (char :name))
                            :holders '[{:name "self" :items []}]})

(defn validate-db-return [return]
  (if-not (nil? return)
    (response-payload 200 {:message "Character created!"})))

(defn create-character [user-external-id char]
  (validate-db-return
    (add-character user-external-id (:_id (save-to-db character-collection (get-char-data char))))))

(defn update-character [user-external-id char-external-id char]
  )

(defn delete-character [user-external-id char]
  )