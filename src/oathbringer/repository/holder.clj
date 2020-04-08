(ns oathbringer.repository.holder
  (:require [oathbringer.util.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))

(defn create-holder-tx [char-id holder]
  {:holder/external-id (nano-id 10)
   :holder/character char-id
   :holder/name (:name holder)
   :holder/main (:main holder)
   :holder/size (:size holder)
   :holder/strength (:strength holder)
   :holder/total-capacity (:total-capacity holder)
   :holder/current-weight (:current-weight holder)
   :holder/overencumbered (:overencumbered holder)})

(defn create-items-holder-tx [holder-external-id item-external-id quantity]
  {:items/holder holder-external-id
   :items/item item-external-id
   :items/quantity quantity})

(defn create-holder [char-external-id holder]
  (transact-single-entity (create-holder-tx {:character/external-id char-external-id} holder)))

(defn add-item [holder-external-id item-external-id quantity]
  (transact-single-entity (create-items-holder-tx {:holder/external-id holder-external-id} {:item/external-id item-external-id} quantity)))

(def find-all-holders-by-user-query '[:find ?holder-name ?size
                                            :in $ ?user
                                            :where [?e :character/user ?user]
                                            [?e :character/external-id ?external-id]
                                            [?e :character/name ?name]
                                            [?h :holder/character ?e]
                                            [?h :holder/name ?holder-name]
                                            [?h :holder/size ?size]])

(def holder-items-schema
  [{:db/ident       :items/holder
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "This characters's holders(e.g. himself, horses, wagons, etc.)"}
   {:db/ident       :items/item
     :db/valueType   :db.type/ref
     :db/cardinality :db.cardinality/one
     :db/doc         "This holder's items"}
   {:db/ident       :items/quantity
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "An item's quantity"}])

(def holder-schema
  [ ;; Character/Holders
   {:db/ident       :holder/external-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "A holder's external ID to be exposed in the API"}
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
    :db/doc         "A holder's strength"}])