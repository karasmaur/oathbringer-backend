(ns oathbringer.repository.character
  (:require [oathbringer.repository.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))




(def character-schema
  [;; Characters
   {:db/ident       :character/external-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "A character's external ID to be exposed in the API"}
   {:db/ident       :character/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "A user's complete name"}
   {:db/ident       :character/holders
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/isComponent true
    :db/doc         "This characters's holders(e.g. himself, horses, wagons, etc.)"}
   ;; Character/Holders
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
   {:db/ident       :holder/items
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/isComponent true
    :db/doc         "This holder's items"}
   ;; Holder/Items
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


