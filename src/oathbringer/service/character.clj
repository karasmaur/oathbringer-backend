(ns oathbringer.service.character
  (:require
    [oathbringer.util.service-util :refer :all]
    [oathbringer.repository.character :refer :all]
    [oathbringer.service.campaign :refer [get-campaign-external-id-from-req]]
    [oathbringer.service.auth :refer [get-user-external-id]]
    [clojure.pprint :as pp]))

(defn get-char-external-id [req]
  (:char-id (:params req)))

(defn add-character-handler [req]
  (let [char (partial get-parameter req)
        user-external-id (get-user-external-id req)
        campaign-external-id (get-campaign-external-id-from-req req)]
    (validate-db-return (create-character user-external-id campaign-external-id char)
                        200
                        "Character created!")))

(defn update-character-handler [req]
  (let [char (get-body-parameters req)
        char-external-id (get-char-external-id req)]
    (validate-db-return (update-character char-external-id char)
                        200
                        "Character updated!")))

(defn delete-character-handler [req]
  (validate-db-return (delete-character (get-user-external-id req) (get-char-external-id req))
                      200
                      "Character deleted!"))

(defn get-all-characters [req]
  (get-all-chars-by-user (get-user-external-id req)))