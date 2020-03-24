(ns oathbringer.repository.item
  (:require [oathbringer.repository.db-util :refer [transact-single-entity query-db convert-datom-to-map]]
            [nano-id.core :refer [nano-id]]))


(def item-schema
  [;; Holder/Items
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