(ns oathbringer.service.character
  (:require
    [oathbringer.util.service-util :refer [get-parameter response-payload]]
    [oathbringer.repository.character :refer [create-character update-character delete-character]]
    [oathbringer.service.auth :refer [get-user-external-id]]
    [clojure.pprint :as pp]))

(defn get-char-external-id [req] (:path-info req))

(defn add-character-handler [req]
  (let [char (partial get-parameter req)
        user-external-id (get-user-external-id req)]
    (create-character user-external-id char)))

(defn update-character-handler [req]
  (let [char (partial get-parameter req)
        user-external-id (get-user-external-id req)
        char-external-id (get-char-external-id req)]
    (pp/pprint char)
    (pp/pprint user-external-id)
    (pp/pprint char-external-id)
    (update-character user-external-id char-external-id char)))

(defn delete-character-handler [req]
  (let [user-external-id (get-user-external-id req)
        char-external-id (get-char-external-id req)]
    (delete-character user-external-id char-external-id)))

(defn get-all-characters [req]
  ())