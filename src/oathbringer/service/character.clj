(ns oathbringer.service.character
  (:require
    [oathbringer.util.service-util :refer [get-parameter response-payload]]
    [oathbringer.repository.character :refer [create-character]]
    [oathbringer.service.auth :refer [get-user-external-id]]))

(defn add-character-handler [req]
  (let [char (partial get-parameter req)
        user-external-id (get-user-external-id req)]
    (create-character user-external-id char)))