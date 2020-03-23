(ns oathbringer.repository.character
  (:require [oathbringer.repository.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))

(defn create-character-tx [user-id character]
  {:character/external-id (nano-id 10)
   :character/user user-id
   :character/name (character :name)})

(defn create-holder-tx [char-id holder]
  {:holder/character char-id
   :holder/name (:name holder)
   :holder/main (:main holder)
   :holder/size (:size holder)
   :holder/strength (:strength holder)
   :holder/total-capacity (:total-capacity holder)
   :holder/current-weight (:current-weight holder)
   :holder/overencumbered (:overencumbered holder)})

(defn create-character [user-external-id character]
  (transact-single-entity (create-character-tx {:user/external-id user-external-id} character)))

(defn create-holder [char-external-id holder]
  (transact-single-entity (create-holder-tx {:character/external-id char-external-id} holder)))

(def character-id-by-external-id-query '[:find ?e
                                   :in $ ?external-id
                                   :where [?e :character/external-id ?external-id]])

;; TODO: This needs to return the chars even if it doesnt have a holder!
(def find-all-chars-holders-by-user-query '[:find ?external-id ?name ?holder-name ?size
                                            :in $ ?user
                                            :where [?e :character/user ?user]
                                                   [?e :character/external-id ?external-id]
                                                   [?e :character/name ?name]
                                                   [?h :holder/character ?e]
                                                   [?h :holder/name ?holder-name]
                                                   [?h :holder/size ?size]])

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
    :db/doc         "A user's complete name"}
   ;; Character/Holders
   {:db/ident       :holder/character
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "This characters's holders(e.g. himself, horses, wagons, etc.)"}
   {:db/ident       :holder/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "A holder's name"}
   {:db/ident       :holder/main
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc         "True if this holder is the main one of the character."}
   {:db/ident       :holder/size
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "The holder's size: e.g.: Tiny, Small, Medium, Large, Huge, Gargantuan"}
   {:db/ident       :holder/strength
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "A holder's strength"}
   {:db/ident       :holder/total-capacity
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one
    :db/doc         "A holder's total capacity"}
   {:db/ident       :holder/current-weight
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one
    :db/doc         "A holder's current carrying weight"}
   {:db/ident       :holder/overencumbered
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "A holder's overencumbered status: 0, 1, 2"}
   {:db/ident       :holder/strength
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "A holder's strength"}
   ;; Holder/Items
   {:db/ident       :item/holder
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "This holder's items"}
   {:db/ident       :item/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's name"}
   {:db/ident       :item/weight
    :db/valueType   :db.type/double
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's weight"}
   {:db/ident       :item/quantity
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's quantity"}])


