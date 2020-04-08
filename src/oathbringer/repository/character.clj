(ns oathbringer.repository.character
  (:require [oathbringer.util.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))

(defn create-character-tx [user-id character]
  {:character/external-id (nano-id 10)
   :character/user user-id
   :character/name (character :name)})

(defn create-character [user-external-id character]
  (transact-single-entity (create-character-tx {:user/external-id user-external-id} character)))

(def character-id-by-external-id-query '[:find ?e
                                   :in $ ?external-id
                                   :where [?e :character/external-id ?external-id]])

(def find-all-chars-by-user-query '[:find ?external-id ?name
                                    :in $ ?user
                                    :where [?e :character/user ?user]
                                           [?e :character/external-id ?external-id]
                                           [?e :character/name ?name]])

(def find-all-chars-query '[:find ?external-id ?name
                            :where
                            [?e :character/external-id ?external-id]
                            [?e :character/name ?name]])

(def character-schema
  [;; Character
   {:db/ident       :character/external-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "A character's external ID to be exposed in the API"}
   {:db/ident        :character/user
    :db/valueType    :db.type/ref
    :db/cardinality  :db.cardinality/one
    :db/doc         "Reference to the user who owns the character"}
   {:db/ident       :character/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "A user's complete name"}])


